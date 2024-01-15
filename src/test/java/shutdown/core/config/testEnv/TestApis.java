package shutdown.core.config.testEnv;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shutdown.core.ShutDown;

import static shutdown.core.config.testEnv.TestApis.API_WITHOUT_LOCAL_SHUTDOWN_CONFIG;
import static shutdown.core.config.testEnv.TestApis.API_WITH_LOCAL_SHUTDOWN_CONFIG;

public class TestApis {

    public static final String API_WITHOUT_LOCAL_SHUTDOWN_CONFIG = "/API_WITHOUT_LOCAL_SHUTDOWN_CONFIG";
    public static final String API_WITH_LOCAL_SHUTDOWN_CONFIG = "/API_WITH_LOCAL_SHUTDOWN_CONFIG";
    public static final HttpStatus GLOBAL_STATUS = HttpStatus.ALREADY_REPORTED;
    public static final String GLOBAL_MESSAGE = "GLOBAL_MESSAGE";
    public static final String CUSTOM_MESSAGE = "CUSTOM_MESSAGE";
}

@ShutDown(force = true)
@RestController
class WithoutLocalShutdownConfigController {

    @GetMapping(value = API_WITHOUT_LOCAL_SHUTDOWN_CONFIG)
    public ResponseEntity<Void> api1() {
        return ResponseEntity.ok().build();
    }
}

@ShutDown(
    force = true,
    message = TestApis.CUSTOM_MESSAGE
)
@RestController
class WithLocalShutdownConfigController {

    @GetMapping(value = API_WITH_LOCAL_SHUTDOWN_CONFIG)
    public ResponseEntity<Void> api1() {
        return ResponseEntity.ok().build();
    }
}
