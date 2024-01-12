package shutdown.annotation;

import org.springframework.context.annotation.Import;
import shutdown.SharedConfigurationReference;

import java.lang.annotation.*;

@Import(SharedConfigurationReference.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableShutDown {
}
