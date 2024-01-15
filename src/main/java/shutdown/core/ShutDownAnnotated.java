package shutdown.core;

import org.springframework.http.HttpStatus;

public class ShutDownAnnotated {

    private final ShutDownConditions conditions;
    private final String message;
    private final HttpStatus status;
    private final String contentType;

    public ShutDownAnnotated(ShutDownConditions conditions, String message, HttpStatus status, String contentType) {
        this.conditions = conditions;
        this.message = message;
        this.status = status;
        this.contentType = contentType;
    }

    public static ShutDownAnnotated of(Class<?> controllerType) {
        var shutDownInfo = controllerType.getAnnotation(ShutDown.class);
        return new ShutDownAnnotated(
            ShutDownConditions.of(shutDownInfo),
            shutDownInfo.message(),
            shutDownInfo.status(),
            shutDownInfo.contentType()
        );
    }

    public String message() {
        return message;
    }

    public HttpStatus status() {
        return status;
    }

    public String contentType() {
        return contentType;
    }

    public ShutDownConditions conditions() {
        return conditions;
    }
}
