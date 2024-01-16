package shutdown.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public ShutDownHandlerMapping(HttpMethod method, List<String> paths) {
        this(List.of(method), paths);
    }

    public ShutDownHandlerMapping(HttpMethod[] methods, List<String> paths) {
        this(List.of(methods), paths);
    }

    public static boolean isHandlerMappingMethod(Method method) {
        return SUPPORTED_HTTP_METHODS.stream()
            .anyMatch(method::isAnnotationPresent);
    }

    public static ShutDownHandlerMapping from(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            return new ShutDownHandlerMapping(
                HttpMethod.GET,
                toUrlPath(
                    method.getAnnotation(GetMapping.class).value(),
                    method.getAnnotation(GetMapping.class).path()
                )
            );
        }

        if (method.isAnnotationPresent(PutMapping.class)) {
            return new ShutDownHandlerMapping(
                HttpMethod.PUT,
                toUrlPath(
                    method.getAnnotation(PutMapping.class).value(),
                    method.getAnnotation(PutMapping.class).path()
                )
            );
        }

        if (method.isAnnotationPresent(DeleteMapping.class)) {
            return new ShutDownHandlerMapping(
                HttpMethod.DELETE,
                toUrlPath(
                    method.getAnnotation(DeleteMapping.class).value(),
                    method.getAnnotation(DeleteMapping.class).path()
                )
            );
        }

        if (method.isAnnotationPresent(PatchMapping.class)) {
            return new ShutDownHandlerMapping(
                HttpMethod.PATCH,
                toUrlPath(
                    method.getAnnotation(PatchMapping.class).value(),
                    method.getAnnotation(PatchMapping.class).path()
                )
            );
        }

        if (method.isAnnotationPresent(RequestMapping.class)) {
            var methods = Arrays.stream(method.getAnnotation(RequestMapping.class).method())
                .map(it -> HttpMethod.valueOf(it.name()))
                .toArray(HttpMethod[]::new);
            return new ShutDownHandlerMapping(
                methods,
                toUrlPath(
                    method.getAnnotation(RequestMapping.class).value(),
                    method.getAnnotation(RequestMapping.class).path()
                )
            );
        }
        throw new ShutDownException("Not a valid handler method");
    }

    private static List<String> toUrlPath(String[] values, String[] paths) {
        if(paths.length != 0) {
            return toUrlPath(paths);
        }
        return toUrlPath(values);
    }

    private static List<String> toUrlPath(String[] paths) {
        return Arrays.stream(paths)
            .map(it -> {
                if(!it.startsWith("/")) {
                    return "/"+it;
                }
                return it;
            })
            .collect(Collectors.toList());
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
