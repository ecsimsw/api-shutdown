package shutdown.core;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ShutDownFilter {

    private final int filterOrder;
    private final String message;
    private final HttpStatus status;
    private final String contentType;

    public ShutDownFilter(int filterOrder, String message, HttpStatus status, String contentType) {
        this.filterOrder = filterOrder;
        this.message = message;
        this.status = status;
        this.contentType = contentType;
    }

    public static ShutDownFilter of(ShutDownGlobalConfig globalConfig, ShutDownAnnotated annotated) {
        return new ShutDownFilter(
            globalConfig.filterOrder(),
            message(globalConfig, annotated.message()),
            status(globalConfig, annotated.status()),
            contentType(globalConfig, annotated.contentType())
        );
    }

    private static String message(ShutDownGlobalConfig globalConfig, String message) {
        if (isDefaultValue(message, "message")) {
            return globalConfig.message();
        }
        return message;
    }

    private static HttpStatus status(ShutDownGlobalConfig globalConfig, HttpStatus status) {
        if (isDefaultValue(status, "status")) {
            return globalConfig.status();
        }
        return status;
    }

    private static String contentType(ShutDownGlobalConfig globalConfig, String contentType) {
        if (isDefaultValue(contentType, "contentType")) {
            return globalConfig.contentType();
        }
        return contentType;
    }

    private static boolean isDefaultValue(Object inputValue, String annotationParamName) {
        try {
            var defaultValue = ShutDown.class.getMethod(annotationParamName).getDefaultValue();
            return defaultValue.equals(inputValue);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public FilterRegistrationBean<Filter> toRegistrationBean(ShutDownHandlerMappings mappingInfos) {
        var shutDownFilterRegistrationBean = new FilterRegistrationBean<>();
        shutDownFilterRegistrationBean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                if (mappingInfos.isMatch(request)) {
                    response.setStatus(status.value());
                    response.setContentType(contentType);
                    response.getWriter().write(message);
                    return;
                }
                filterChain.doFilter(request, response);
            }
        });
        shutDownFilterRegistrationBean.setOrder(filterOrder);
        shutDownFilterRegistrationBean.setEnabled(true);
        shutDownFilterRegistrationBean.addUrlPatterns(mappingInfos.paths());
        return shutDownFilterRegistrationBean;
    }
}
