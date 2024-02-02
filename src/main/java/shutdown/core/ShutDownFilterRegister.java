package shutdown.core;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutDownFilterRegister.class);

    private Environment environment;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        var globalConfig = getGlobalConfiguration(beanFactory);

        for (var controller : shutDownControllerTypes(beanFactory)) {
            var annotated = ShutDownAnnotated.of(controller);
            LOGGER.info("hihiihihihi");
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
            beanType -> {
                boolean b = hasBeanInFactory(beanFactory, beanType);
                LOGGER.info("hihih i " + b);
                return b;
            }
        );
    }

    private boolean hasBeanInFactory(ConfigurableListableBeanFactory beanFactory, Class<?> beanType) {
        try {
            Arrays.stream(beanFactory.getBeanDefinitionNames())
                    .forEach(it -> LOGGER.info("bean def i " + it));
            String[] beanNamesForType = beanFactory.getBeanNamesForType(beanType);
            Arrays.stream(beanNamesForType)
                    .forEach(it -> LOGGER.info("bean name" + it));
            beanFactory.getBean(beanType);
            return true;
        } catch (BeansException e) {
            return false;
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

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
