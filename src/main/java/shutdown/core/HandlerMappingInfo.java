package shutdown.core;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class HandlerMappingInfo {

    private static final Set<Class<? extends Annotation>> SUPPORTED_HTTP_METHODS = Set.of(
        GetMapping.class, PutMapping.class, DeleteMapping.class, PatchMapping.class
    );

    private final RequestMethod[] methods;
    private final String[] paths;
    // TODO :: headers, consume ...

    public HandlerMappingInfo(RequestMethod[] methods, String[] paths) {
        this.methods = methods;
        this.paths = paths;
    }

    public HandlerMappingInfo(RequestMethod methods, String[] paths) {
        this(new RequestMethod[]{methods}, paths);
    }

    public static boolean isHandlerMappingMethod(Method method) {
        return SUPPORTED_HTTP_METHODS.stream()
            .anyMatch(method::isAnnotationPresent);
    }

    public static HandlerMappingInfo from(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            var apiPaths = method.getAnnotation(GetMapping.class).value();
            return new HandlerMappingInfo(RequestMethod.GET, apiPaths);
        }

        if (method.isAnnotationPresent(PutMapping.class)) {
            var apiPaths = method.getAnnotation(PutMapping.class).value();
            return new HandlerMappingInfo(RequestMethod.PUT, apiPaths);
        }

        if (method.isAnnotationPresent(DeleteMapping.class)) {
            var apiPaths = method.getAnnotation(DeleteMapping.class).value();
            return new HandlerMappingInfo(RequestMethod.DELETE, apiPaths);
        }

        if (method.isAnnotationPresent(PatchMapping.class)) {
            var apiPaths = method.getAnnotation(PatchMapping.class).value();
            return new HandlerMappingInfo(RequestMethod.PATCH, apiPaths);
        }

        if (method.isAnnotationPresent(RequestMapping.class)) {
            var apiPaths = method.getAnnotation(RequestMapping.class).value();
            var methods = Arrays.stream(method.getAnnotation(RequestMapping.class).method())
                .toArray(RequestMethod[]::new);
            return new HandlerMappingInfo(methods, apiPaths);
        }
        throw new IllegalArgumentException("Not a valid handler method");
    }

    public RequestMappingInfo toRequestMappingInfo(RequestMappingInfo.BuilderConfiguration configuration) {
        return RequestMappingInfo
            .paths(paths)
            .methods(methods)
            .options(configuration)
            .build();
    }
}
