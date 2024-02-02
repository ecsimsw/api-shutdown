package shutdown.core;

import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShutDownFilterRegister implements BeanFactoryPostProcessor, EnvironmentAware {

    private Environment environment;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        var globalConfig = getGlobalConfiguration(beanFactory);
        for (var controller : shutDownControllerTypes(beanFactory)) {
            var annotated = ShutDownAnnotated.of(controller);
            if(isShutDownCondition(annotated, beanFactory)) {
                var handlerMappings = ShutDownHandlerMappings.of(controller);
                var shutDownFilter = ShutDownFilter.of(globalConfig, annotated);
                beanFactory.registerSingleton(
                    globalConfig.nextFilterBeanName(),
                    shutDownFilter.toRegistrationBean(handlerMappings)
                );
            }
        }
    }

    private boolean isShutDownCondition(ShutDownAnnotated annotated, ConfigurableListableBeanFactory beanFactory) {
        return annotated.conditions().isCondition(
            profile -> List.of(environment.getActiveProfiles()).contains(profile),
            property -> environment.containsProperty(property),
            beanType -> hasBeanInFactory(beanFactory, beanType)
        );
    }

    // XXX :: beanFactory.getBean is not working at other packages. Failed to get bean class file.
    private boolean hasBeanInFactory(ConfigurableListableBeanFactory beanFactory, Class<?> beanType) {
        var beanNamesForType = beanFactory.getBeanNamesForType(beanType);
        return Arrays.stream(beanNamesForType)
            .anyMatch(beanFactory::containsBean);
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

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
