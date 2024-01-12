package shutdown.core;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import shutdown.ShutDownException;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class ShutDownMappingInfo {

    private static final Set<Class<? extends Annotation>> SUPPORTED_HTTP_METHODS = Set.of(
        GetMapping.class, PutMapping.class, DeleteMapping.class, PatchMapping.class
    );

    private final HttpMethod[] methods;
    private final String[] paths;

    public ShutDownMappingInfo(HttpMethod[] methods, String[] paths) {
        this.methods = methods;
        this.paths = paths;
    }

    public ShutDownMappingInfo(HttpMethod methods, String[] paths) {
        this(new HttpMethod[]{methods}, paths);
    }

    public static boolean isHandlerMappingMethod(Method method) {
        return SUPPORTED_HTTP_METHODS.stream()
            .anyMatch(method::isAnnotationPresent);
    }

    public static ShutDownMappingInfo from(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            var apiPaths = method.getAnnotation(GetMapping.class).value();
            return new ShutDownMappingInfo(HttpMethod.GET, apiPaths);
        }

        if (method.isAnnotationPresent(PutMapping.class)) {
            var apiPaths = method.getAnnotation(PutMapping.class).value();
            return new ShutDownMappingInfo(HttpMethod.PUT, apiPaths);
        }

        if (method.isAnnotationPresent(DeleteMapping.class)) {
            var apiPaths = method.getAnnotation(DeleteMapping.class).value();
            return new ShutDownMappingInfo(HttpMethod.DELETE, apiPaths);
        }

        if (method.isAnnotationPresent(PatchMapping.class)) {
            var apiPaths = method.getAnnotation(PatchMapping.class).value();
            return new ShutDownMappingInfo(HttpMethod.PATCH, apiPaths);
        }

        if (method.isAnnotationPresent(RequestMapping.class)) {
            var apiPaths = method.getAnnotation(RequestMapping.class).value();
            var methods = Arrays.stream(method.getAnnotation(RequestMapping.class).method())
                .map(it -> HttpMethod.valueOf(it.name()))
                .toArray(HttpMethod[]::new);
            return new ShutDownMappingInfo(methods, apiPaths);
        }
        throw new ShutDownException("Not a valid handler method");
    }

    public boolean isMatch(String path, HttpMethod method) {
        return Arrays.asList(paths).contains(path)
            && Arrays.asList(methods).contains(method);
    }

    public boolean isMatch(HttpServletRequest request) {
        return isMatch(request.getRequestURI(), HttpMethod.valueOf(request.getMethod()));
    }
}
