package shutdown.testEnv;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEnvController {

    @GetMapping("/api/test")
    public ResponseEntity<String> testApi() {
        return ResponseEntity.ok("test");
    }
}
