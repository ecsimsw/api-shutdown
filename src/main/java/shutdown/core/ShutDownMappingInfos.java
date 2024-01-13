package shutdown.core;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class ShutDownMappingInfos {

    private final Map<String, List<ShutDownMappingInfo>> mappingInfos;

    public ShutDownMappingInfos(Map<String, List<ShutDownMappingInfo>> mappingInfos) {
        this.mappingInfos = mappingInfos;
    }

    public static ShutDownMappingInfos of(Class<?> controller) {
        Map<String, List<ShutDownMappingInfo>> mappingInfos = new HashMap<>();
        Arrays.stream(controller.getMethods())
            .filter(ShutDownMappingInfo::isHandlerMappingMethod)
            .map(ShutDownMappingInfo::from)
            .forEach(info -> {
                for (var path : info.getPaths()) {
                    var infos = mappingInfos.getOrDefault(path, new ArrayList<>());
                    infos.add(info);
                    mappingInfos.put(path, infos);
                }
            });
        return new ShutDownMappingInfos(mappingInfos);
    }

    public boolean isMatch(HttpServletRequest request) {
        var path = request.getRequestURI();
        return mappingInfos.get(path).stream()
            .anyMatch(it -> it.isMatch(request));
    }

    public String[] paths() {
        return mappingInfos.keySet()
            .toArray(String[]::new);
    }
}
