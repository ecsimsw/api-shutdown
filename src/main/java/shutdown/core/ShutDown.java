package shutdown.core;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ShutDown {

    // Shut down when all the profiles are activated
    String[] conditionOnActiveProfile() default {};

    // Shut down when all the beans exist
    Class<?>[] conditionOnBean() default {};

    // Shut down when all the beans not exist
    Class<?>[] conditionOnMissingBean() default {};

    // Response body message
    String message() default "The server is currently unable to handle the request due to a temporary overloading or maintenance of the server.";

    // Response http status
    HttpStatus status() default HttpStatus.SERVICE_UNAVAILABLE;

    // Response content type
    String contentType() default MediaType.APPLICATION_JSON_VALUE;

    // Force shutdown ignoring other conditions
    boolean force() default false;
}
