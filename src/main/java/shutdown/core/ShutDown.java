package shutdown.core;

import org.springframework.http.HttpStatus;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ShutDown {

    // Shut down when all beans exist
    Class<?>[] conditionOnBean() default {};

    // Shut down when all beans not exist
    Class<?>[] conditionOnMissingBean() default {};

    // Shut down when all profile exist
    String[] conditionOnProfile() default "";

    // Shut down when all profile not exist
    String[] conditionMissingOnProfile() default "";

    // Response body message of shut down api
    String[] message() default "The server is currently unable to handle the request due to a temporary overloading or maintenance of the server.";

    // Response http status of shut down api
    HttpStatus status() default HttpStatus.SERVICE_UNAVAILABLE;
}
