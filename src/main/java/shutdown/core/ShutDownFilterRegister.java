package shutdown.core;

import org.reflections.Reflections;
import org.springframework.beans.BeansException;
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
import java.util.stream.Collectors;

@Component
public class ShutDownFilterRegister implements BeanFactoryPostProcessor {

    private final int filterOrder = 0;
    private final String filterPrefix = "shutdownFilter-";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        var shutDownControllers = AutoConfigurationPackages.get(beanFactory).stream()
            .flatMap(it -> new Reflections(it).getTypesAnnotatedWith(ShutDown.class).stream())
            .collect(Collectors.toList());

        var filterPostfix = 1;
        for (var controller : shutDownControllers) {
            var shutDownInfo = ShutDownInfo.of(controller);
            if (shutDownInfo.isShutDown(beanFactory)) {
                var shutDownFilter = shutDownFilter(shutDownInfo, controller);
                beanFactory.registerSingleton(filterPrefix + filterPostfix++, shutDownFilter);
            }
        }
    }

    private FilterRegistrationBean<Filter> shutDownFilter(ShutDownInfo shutDownInfo, Class<?> controller) {
        var mappingInfos = ShutDownMappingInfos.of(controller);
        var shutDownFilterRegistrationBean = new FilterRegistrationBean<>();
        shutDownFilterRegistrationBean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                if (mappingInfos.isMatch(request)) {
                    response.setStatus(shutDownInfo.status().value());
                    response.setContentType(shutDownInfo.mediaType().getType());
                    response.getWriter().write(shutDownInfo.message());
                    return;
                }
                filterChain.doFilter(request, response);
            }
        });
        shutDownFilterRegistrationBean.setEnabled(true);
        shutDownFilterRegistrationBean.setOrder(filterOrder);
        shutDownFilterRegistrationBean.addUrlPatterns(mappingInfos.paths());
        return shutDownFilterRegistrationBean;
    }
}
