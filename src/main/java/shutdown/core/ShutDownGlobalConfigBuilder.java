package shutdown.core;

import org.springframework.http.HttpStatus;

import java.util.Optional;

public class ShutDownGlobalConfigBuilder {

    private Optional<Integer> filterOrder = Optional.empty();
    private Optional<String> filterPrefix = Optional.empty();
    private Optional<String> message = Optional.empty();
    private Optional<HttpStatus> status = Optional.empty();
    private Optional<String> contentType = Optional.empty();

    public ShutDownGlobalConfigBuilder filterOrder(int filterOrder) {
        this.filterOrder = Optional.of(filterOrder);
        return this;
    }

    public ShutDownGlobalConfigBuilder filterPrefix(String filterPrefix) {
        this.filterPrefix = Optional.of(filterPrefix);
        return this;
    }

    public ShutDownGlobalConfigBuilder message(String message) {
        this.message = Optional.of(message);
        return this;
    }

    public ShutDownGlobalConfigBuilder status(HttpStatus status) {
        this.status = Optional.of(status);
        return this;
    }

    public ShutDownGlobalConfigBuilder contentType(String contentType) {
        this.contentType = Optional.of(contentType);
        return this;
    }

    public ShutDownGlobalConfig build() {
        var shutDownConfiguration = ShutDownGlobalConfig.defaultValue();
        filterPrefix.ifPresent(shutDownConfiguration::setFilterPrefix);
        filterOrder.ifPresent(shutDownConfiguration::setFilterOrder);
        message.ifPresent(shutDownConfiguration::setMessage);
        status.ifPresent(shutDownConfiguration::setStatus);
        contentType.ifPresent(shutDownConfiguration::setContentType);
        return shutDownConfiguration;
    }
}
