package shutdown.core;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ShutDownHandlerMapping {

    private static final Set<Class<? extends Annotation>> SUPPORTED_HTTP_METHODS = Set.of(
        GetMapping.class, PutMapping.class, DeleteMapping.class, PatchMapping.class, RequestMapping.class
    );

    private final List<HttpMethod> methods;
    private final List<String> paths;

    public ShutDownHandlerMapping(List<HttpMethod> methods, List<String> paths) {
        this.methods = methods;
        this.paths = paths;
    }

    public ShutDownHandlerMapping(HttpMethod method, String[] paths) {
        this(List.of(method), List.of(paths));
    }

    public ShutDownHandlerMapping(HttpMethod[] methods, String[] paths) {
        this(List.of(methods), List.of(paths));
    }

    public static boolean isHandlerMappingMethod(Method method) {
        return SUPPORTED_HTTP_METHODS.stream()
            .anyMatch(method::isAnnotationPresent);
    }

    public static ShutDownHandlerMapping from(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            return new ShutDownHandlerMapping(
                HttpMethod.GET,
                method.getAnnotation(GetMapping.class).value()
            );
        }

        if (method.isAnnotationPresent(PutMapping.class)) {
            return new ShutDownHandlerMapping(
                HttpMethod.PUT,
                method.getAnnotation(PutMapping.class).value()
            );
        }

        if (method.isAnnotationPresent(DeleteMapping.class)) {
            return new ShutDownHandlerMapping(
                HttpMethod.DELETE,
                method.getAnnotation(DeleteMapping.class).value()
            );
        }

        if (method.isAnnotationPresent(PatchMapping.class)) {
            return new ShutDownHandlerMapping(
                HttpMethod.PATCH,
                method.getAnnotation(PatchMapping.class).value()
            );
        }

        if (method.isAnnotationPresent(RequestMapping.class)) {
            var methods = Arrays.stream(method.getAnnotation(RequestMapping.class).method())
                .map(it -> HttpMethod.valueOf(it.name()))
                .toArray(HttpMethod[]::new);
            return new ShutDownHandlerMapping(methods, method.getAnnotation(RequestMapping.class).value());
        }
        throw new ShutDownException("Not a valid handler method");
    }

    public boolean isMatch(String path, HttpMethod method) {
        // TODO :: To be more specific
        return paths.contains(path) && methods.contains(method);
    }

    public boolean isMatch(HttpServletRequest request) {
        return isMatch(request.getRequestURI(), HttpMethod.valueOf(request.getMethod()));
    }

    public List<String> getPaths() {
        return paths;
    }
}
