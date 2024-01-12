package shutdown.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ShutDownFilterRegister implements BeanFactoryPostProcessor {

    int filterOrder = 0;
    String filterUrlPrefix = "/*";
    String filterPrefix = "shutdownFilter-";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        var shutDownControllers = AutoConfigurationPackages.get(beanFactory).stream()
            .flatMap(it -> new Reflections(it).getTypesAnnotatedWith(ShutDown.class).stream())
            .collect(Collectors.toList());

        var filterPostfix = 1;
        for (var controller : shutDownControllers) {
            if (!isShutDownCondition(controller)) {
                continue;
            }
            beanFactory.registerSingleton(filterPrefix + filterPostfix++, shutDownFilter(controller));
        }
    }

    private boolean isShutDownCondition(Class<?> controller) {
        var shutDownInfo = controller.getAnnotation(ShutDown.class);
        // TODO
        return true;
    }

    private FilterRegistrationBean<Filter> shutDownFilter(Class<?> controller) {
        var shutDownMappingInfos = Arrays.stream(controller.getMethods())
            .filter(ShutDownMappingInfo::isHandlerMappingMethod)
            .map(ShutDownMappingInfo::from)
            .collect(Collectors.toList());
        var shutDownInfo = controller.getAnnotation(ShutDown.class);
        return shutDownFilter(
            shutDownInfo.message(),
            shutDownInfo.status(),
            shutDownMappingInfos
        );
    }

    private FilterRegistrationBean<Filter> shutDownFilter(String message, HttpStatus status, List<ShutDownMappingInfo> mappingInfos) {
        var shutDownFilterRegistrationBean = new FilterRegistrationBean<>();
        shutDownFilterRegistrationBean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                if (mappingInfos.stream().anyMatch(it -> it.isMatch(request))) {
                    filterChain.doFilter(request, response);
                    return;
                }
                response.setStatus(status.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getWriter(), Map.of("message", message));
            }
        });
        shutDownFilterRegistrationBean.setEnabled(true);
        shutDownFilterRegistrationBean.setOrder(filterOrder);
        shutDownFilterRegistrationBean.addUrlPatterns(filterUrlPrefix);
        return shutDownFilterRegistrationBean;
    }
}
