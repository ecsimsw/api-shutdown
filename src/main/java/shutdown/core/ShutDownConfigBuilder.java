package shutdown.core;

import org.springframework.http.HttpStatus;

import java.util.Optional;

public class ShutDownConfigBuilder {

    private Optional<Integer> filterOrder = Optional.empty();
    private Optional<String> filterPrefix = Optional.empty();
    private Optional<String> message = Optional.empty();
    private Optional<HttpStatus> status = Optional.empty();
    private Optional<String> contentType = Optional.empty();
    private Optional<Boolean> force = Optional.empty();

    public ShutDownConfigBuilder filterOrder(int filterOrder) {
        this.filterOrder = Optional.of(filterOrder);
        return this;
    }

    public ShutDownConfigBuilder filterPrefix(String filterPrefix) {
        this.filterPrefix = Optional.of(filterPrefix);
        return this;
    }

    public ShutDownConfigBuilder message(String message) {
        this.message = Optional.of(message);
        return this;
    }

    public ShutDownConfigBuilder status(HttpStatus status) {
        this.status = Optional.of(status);
        return this;
    }

    public ShutDownConfigBuilder contentType(String contentType) {
        this.contentType = Optional.of(contentType);
        return this;
    }

    public ShutDownConfigBuilder force(boolean force) {
        this.force = Optional.of(force);
        return this;
    }

    public ShutDownGlobalConfig build() {
        var shutDownConfiguration = ShutDownGlobalConfig.defaultValue();
        filterPrefix.ifPresent(shutDownConfiguration::setFilterPrefix);
        filterOrder.ifPresent(shutDownConfiguration::setFilterOrder);
        message.ifPresent(shutDownConfiguration::setMessage);
        status.ifPresent(shutDownConfiguration::setStatus);
        contentType.ifPresent(shutDownConfiguration::setContentType);
        force.ifPresent(shutDownConfiguration::setForce);
        return shutDownConfiguration;
    }
}
