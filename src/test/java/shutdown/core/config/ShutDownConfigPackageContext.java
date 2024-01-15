package shutdown.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/*
Isolate the test environment from other test packages
 */

@ComponentScan(
    basePackages = {"shutdown.core"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "shutdown.core.filter.*")
)
@Configuration

public class ShutDownConfigPackageContext {
}
