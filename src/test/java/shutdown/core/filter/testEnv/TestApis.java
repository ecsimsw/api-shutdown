package shutdown.core.filter.testEnv;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import shutdown.core.ShutDown;

import static shutdown.core.filter.testEnv.TestApis.DEFAULT_NORMAL_MESSAGE;
import static shutdown.core.filter.testEnv.TestApis.SHUTDOWN_MESSAGE;

public class TestApis {

    public static final String API_TO_BE_SHUTDOWN = "/API_TO_BE_SHUTDOWN";
    public static final String API_TO_BE_SHUTDOWN_MULTIPLE_MAPPINGS_1 = "/API_TO_BE_SHUTDOWN_MULTIPLE_MAPPINGS_1";
    public static final String API_TO_BE_SHUTDOWN_MULTIPLE_MAPPINGS_2 = "/API_TO_BE_SHUTDOWN_MULTIPLE_MAPPINGS_2";
    public static final String API_TO_BE_SHUTDOWN_BY_PROFILE_CONDITION = "/API_TO_BE_SHUTDOWN_BY_PROFILE_CONDITION";
    public static final String API_TO_BE_SHUTDOWN_BY_BEAN_CONDITION = "/API_TO_BE_SHUTDOWN_BY_BEAN_CONDITION";
    public static final String API_NOT_TO_BE_SHUTDOWN_BY_BEAN_EXISTS = "/API_NOT_TO_BE_SHUTDOWN_BY_BEAN_EXISTS";
    public static final String API_TO_BE_SHUTDOWN_BY_MISSING_BEAN = "/API_TO_BE_SHUTDOWN_BY_MISSING_BEAN";
    public static final String API_TO_BE_SHUTDOWN_BY_FORCE = "/API_TO_BE_SHUTDOWN_BY_FORCE";
    public static final String API_NOT_DEFINED = "/API_NOT_DEFINED";

    public static final String SHUTDOWN_MESSAGE = "SHUTDOWN_MESSAGE";
    public static final String DEFAULT_NORMAL_MESSAGE = "OK";
    public static final HttpStatus DEFAULT_SHUTDOWN_STATUS = HttpStatus.SERVICE_UNAVAILABLE;
}

@ShutDown(
    force = true,
    message = SHUTDOWN_MESSAGE,
    status = HttpStatus.ALREADY_REPORTED
)
@RestController
class ShutDownController {

    @GetMapping(value = TestApis.API_TO_BE_SHUTDOWN)
    public ResponseEntity<String> api1() {
        return ResponseEntity.ok(DEFAULT_NORMAL_MESSAGE);
    }
}

@ShutDown(force = true)
@RestController
class ShutDownMultipleMappingsController {

    @RequestMapping(
        value = {
            TestApis.API_TO_BE_SHUTDOWN_MULTIPLE_MAPPINGS_1,
            TestApis.API_TO_BE_SHUTDOWN_MULTIPLE_MAPPINGS_2
        },
        method = { RequestMethod.GET, RequestMethod.POST }
    )
    public ResponseEntity<String> api1() {
        return ResponseEntity.ok(DEFAULT_NORMAL_MESSAGE);
    }
}

@ShutDown(conditionOnBean = ShutDownConditionBeanController.class)
@RestController
class ShutDownConditionBeanController {

    @GetMapping(value = TestApis.API_TO_BE_SHUTDOWN_BY_BEAN_CONDITION)
    public ResponseEntity<String> api1() {
        return ResponseEntity.ok(DEFAULT_NORMAL_MESSAGE);
    }
}

@ShutDown(conditionOnActiveProfile = "test")
@RestController
class ShutDownConditionProfileController {

    @GetMapping(value = TestApis.API_TO_BE_SHUTDOWN_BY_PROFILE_CONDITION)
    public ResponseEntity<String> api1() {
        return ResponseEntity.ok(DEFAULT_NORMAL_MESSAGE);
    }
}

@ShutDown(force = true)
@RestController
class ShutDownConditionByForce {

    @GetMapping(value = TestApis.API_TO_BE_SHUTDOWN_BY_FORCE)
    public ResponseEntity<String> api1() {
        return ResponseEntity.ok(DEFAULT_NORMAL_MESSAGE);
    }
}

@ShutDown(conditionOnMissingBean = NotRegisteredAsBean.class)
@RestController
class ShutDownConditionOnMissingBean {

    @GetMapping(value = TestApis.API_TO_BE_SHUTDOWN_BY_MISSING_BEAN)
    public ResponseEntity<String> api1() {
        return ResponseEntity.ok(DEFAULT_NORMAL_MESSAGE);
    }
}

@ShutDown(conditionOnBean = {ShutDownConditionOnMissingBean.class, NotRegisteredAsBean.class})
@RestController
class ShutDownByConditionBeanFailed {

    @GetMapping(value = TestApis.API_NOT_TO_BE_SHUTDOWN_BY_BEAN_EXISTS)
    public ResponseEntity<String> api1() {
        return ResponseEntity.ok(DEFAULT_NORMAL_MESSAGE);
    }
}
