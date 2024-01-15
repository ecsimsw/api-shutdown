package shutdown.core.config.testEnv;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import shutdown.core.ShutDownGlobalConfig;
import shutdown.core.ShutDownGlobalConfigBuilder;

import static shutdown.core.config.testEnv.TestApis.GLOBAL_MESSAGE;
import static shutdown.core.config.testEnv.TestApis.GLOBAL_STATUS;

@Configuration
public
class ShutDownConfig {

    public ShutDownConfig() {
    }

    @Bean
    public ShutDownGlobalConfig shutDown() {
        return new ShutDownGlobalConfigBuilder()
            .filterOrder(0)
            .filterPrefix("myShutDownFilter")
            .message(GLOBAL_MESSAGE)
            .status(GLOBAL_STATUS)
            .build();
    }
}