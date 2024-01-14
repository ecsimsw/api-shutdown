package shutdown.core;

import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShutDownFilterRegister implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        var globalConfig = getGlobalConfiguration(beanFactory);
        for (var controller : shutDownControllerTypes(beanFactory)) {
            var shutDownInfo = ShutDownInfo.of(globalConfig, controller);
            var shutDownMappingInfos = ShutDownMappingInfos.of(controller);
            var shutDownFilter = shutDownFilter(shutDownInfo, shutDownMappingInfos);
            shutDownFilter.setOrder(shutDownInfo.filterOrder());
            shutDownFilter.setEnabled(shutDownInfo.isShutDown(beanFactory));
            beanFactory.registerSingleton(shutDownInfo.filterName(), shutDownFilter);
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

    private FilterRegistrationBean<Filter> shutDownFilter(ShutDownInfo shutDownInfo, ShutDownMappingInfos mappingInfos) {
        var shutDownFilterRegistrationBean = new FilterRegistrationBean<>();
        shutDownFilterRegistrationBean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                if (mappingInfos.isMatch(request)) {
                    response.setStatus(shutDownInfo.status().value());
                    response.setContentType(shutDownInfo.produce());
                    response.getWriter().write(shutDownInfo.message());
                    return;
                }
                filterChain.doFilter(request, response);
            }
        });
        shutDownFilterRegistrationBean.addUrlPatterns(mappingInfos.paths());
        return shutDownFilterRegistrationBean;
    }
}
