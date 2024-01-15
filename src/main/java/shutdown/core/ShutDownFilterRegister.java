package shutdown.core;

import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShutDownFilterRegister implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        var globalConfig = getGlobalConfiguration(beanFactory);
        for (var controller : shutDownControllerTypes(beanFactory)) {
            var annotated = ShutDownAnnotated.of(controller);
            if(annotated.isCondition(beanFactory)) {
                var handlerMappings = ShutDownHandlerMappings.of(controller);
                var shutDownFilter = ShutDownFilter.of(globalConfig, annotated);
                beanFactory.registerSingleton(
                    globalConfig.nextFilterBeanName(),
                    shutDownFilter.toRegistrationBean(handlerMappings)
                );
            }
        }
    }

    private ShutDownGlobalConfig getGlobalConfiguration(BeanFactory beanFactory) {
        try {
            return beanFactory.getBean(ShutDownGlobalConfig.class);
        } catch (BeansException e) {
            return ShutDownGlobalConfig.defaultValue();
        }
    }

    private List<Class<?>> shutDownControllerTypes(ConfigurableListableBeanFactory beanFactory) {
        return AutoConfigurationPackages.get(beanFactory).stream()
            .flatMap(it -> new Reflections(it).getTypesAnnotatedWith(ShutDown.class).stream())
            .collect(Collectors.toList());
    }
}
