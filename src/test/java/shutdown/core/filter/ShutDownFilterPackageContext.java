package shutdown.core.filter;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@EnableAutoConfiguration
@ComponentScan(
    basePackages = {"shutdown.core"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "shutdown.core.config.*")
)
@Configuration
public class ShutDownFilterPackageContext {
}
