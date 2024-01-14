package shutdown;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = {"shutdown.core"})
public class ShutDownCorePackageContext {
}
