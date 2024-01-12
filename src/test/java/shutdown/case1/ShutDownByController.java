package shutdown.case1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shutdown.core.ShutDown;

@ShutDown(conditionOnProfile = "dev")
@RestController
public class ShutDownByController {

    public static final String TO_BE_SHUT_DOWN_PATH_1 = "/1";
    public static final String TO_BE_SHUT_DOWN_PATH_2 = "/2";

    @GetMapping(value = TO_BE_SHUT_DOWN_PATH_1)
    public ResponseEntity<Void> toBeShutDown1() {
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = TO_BE_SHUT_DOWN_PATH_2)
    public ResponseEntity<Void> toBeShutDown2() {
        return ResponseEntity.ok().build();
    }
}

