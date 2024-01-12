package shutdown.core;

import org.reflections.Reflections;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class ShutDownHandler {

    private final ApplicationContext context;
    private final RequestMappingHandlerMapping requestHandlerMapping;
    private final ShutDownController shutDownController;

    public ShutDownHandler(
        ApplicationContext context,
        @Qualifier(value = "requestMappingHandlerMapping")
            RequestMappingHandlerMapping requestHandlerMapping,
        ShutDownController shutDownController
    ) {
        this.context = context;
        this.requestHandlerMapping = requestHandlerMapping;
        this.shutDownController = shutDownController;
    }

    @PostConstruct
    public void handle() {
        registerShutDownApiByController();
    }

    private void registerShutDownApiByController() {
        var shutDownControllers = AutoConfigurationPackages.get(context).stream()
            .flatMap(it -> new Reflections(it).getTypesAnnotatedWith(ShutDown.class).stream())
            .filter(it -> !isAlreadyBean(it))
            .collect(Collectors.toList());

        for (var controllerClass : shutDownControllers) {
            Arrays.stream(controllerClass.getMethods())
                .filter(HandlerMappingInfo::isHandlerMappingMethod)
                .map(HandlerMappingInfo::from)
                .forEach(this::addHandler);
        }
    }

    private void deleteControllerBean(Class<?> controllerClass) {
        var registry = (BeanDefinitionRegistry) context;
        for(var beanName : context.getBeanNamesForType(controllerClass)) {
            if(context.getBean(beanName).getClass().isAnnotationPresent(ShutDown.class)) {
                registry.removeBeanDefinition(beanName);
            }
        }
    }

    private boolean isAlreadyBean(Class<?> type) {
        try {
            context.getBean(type);
            return true;
        } catch (NoSuchBeanDefinitionException e) {
            return false;
        }
    }

    private void addHandler(HandlerMappingInfo handlerMappingInfo) {
        try {
            var options = new RequestMappingInfo.BuilderConfiguration();
            var patternParser = requestHandlerMapping.getPatternParser();
            options.setPatternParser(patternParser);
            requestHandlerMapping.registerMapping(
                handlerMappingInfo.toRequestMappingInfo(options),
                shutDownController,
                shutDownController.getDefaultHandlerMethod()
            );
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}

@RestController
class ShutDownController {

    public ResponseEntity<String> noticeShutDown() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("현재 사용할 수 없는 Api 입니다.");
    }

    public Method getDefaultHandlerMethod() throws NoSuchMethodException {
        return getClass().getMethod("noticeShutDown");
    }
}
