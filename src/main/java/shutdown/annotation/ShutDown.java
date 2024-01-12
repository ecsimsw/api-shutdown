package shutdown.annotation;

import org.springframework.http.HttpStatus;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ShutDown {

    String[] message() default "The server is currently unable to handle the request due to a temporary overloading or maintenance of the server.";

    HttpStatus status() default HttpStatus.SERVICE_UNAVAILABLE;
}
