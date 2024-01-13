package shutdown;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Import(SharedConfigurationReference.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableShutDown {
}
