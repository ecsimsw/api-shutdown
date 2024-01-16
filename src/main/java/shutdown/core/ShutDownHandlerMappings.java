package shutdown.core;

import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

public class ShutDownHandlerMappings {

    private final Map<String, List<ShutDownHandlerMapping>> mappingInfosByPath;

    public ShutDownHandlerMappings(Map<String, List<ShutDownHandlerMapping>> mappingInfosByPath) {
        this.mappingInfosByPath = mappingInfosByPath;
    }

    public static ShutDownHandlerMappings of(Class<?> controller) {
        Map<String, List<ShutDownHandlerMapping>> mappingInfos = new HashMap<>();
        Arrays.stream(controller.getMethods())
            .filter(ShutDownHandlerMapping::isHandlerMappingMethod)
            .map(ShutDownHandlerMapping::from)
            .forEach(info -> {
                for (var path : info.getPaths()) {
                    var infos = mappingInfos.getOrDefault(path, new ArrayList<>());
                    infos.add(info);
                    mappingInfos.put(path, infos);
                }
            });
        return new ShutDownHandlerMappings(mappingInfos);
    }

    public boolean isMatch(HttpServletRequest request) {
        var path = request.getRequestURI();
        return mappingInfosByPath.get(path).stream()
            .anyMatch(it -> it.isMatch(request));
    }

    public String[] paths() {
        return mappingInfosByPath.keySet()
            .toArray(String[]::new);
    }
}
