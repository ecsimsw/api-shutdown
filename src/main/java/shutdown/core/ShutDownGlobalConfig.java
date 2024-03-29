package shutdown.core;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.concurrent.atomic.AtomicInteger;

public class ShutDownGlobalConfig {

    public static final int DEFAULT_FILTER_ORDER = 0;
    public static final String DEFAULT_FILTER_PREFIX = "shutdownFilter-";
    public static final String DEFAULT_MESSAGE = "The server is currently unable to handle the request due to a temporary overloading or maintenance of the server.";
    public static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.SERVICE_UNAVAILABLE;
    public static final String DEFAULT_CONTENT_TYPE =  MediaType.APPLICATION_JSON_VALUE;

    private final AtomicInteger filterPostfix = new AtomicInteger(0);

    private int filterOrder;
    private String filterPrefix;
    private String message;
    private HttpStatus status;
    private String contentType;

    public static ShutDownGlobalConfig defaultValue() {
        return new ShutDownGlobalConfig(
            DEFAULT_FILTER_ORDER,
            DEFAULT_FILTER_PREFIX,
            DEFAULT_MESSAGE,
            DEFAULT_HTTP_STATUS,
            DEFAULT_CONTENT_TYPE
        );
    }

    public ShutDownGlobalConfig(int filterOrder, String filterPrefix, String message, HttpStatus status, String contentType) {
        this.filterOrder = filterOrder;
        this.filterPrefix = filterPrefix;
        this.message = message;
        this.status = status;
        this.contentType = contentType;
    }

    public String nextFilterBeanName() {
        return filterPrefix + filterPostfix.incrementAndGet();
    }

    public int filterOrder() {
        return filterOrder;
    }

    public void setFilterOrder(int filterOrder) {
        this.filterOrder = filterOrder;
    }

    public void setFilterPrefix(String filterPrefix) {
        this.filterPrefix = filterPrefix;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String contentType() {
        return contentType;
    }
}
