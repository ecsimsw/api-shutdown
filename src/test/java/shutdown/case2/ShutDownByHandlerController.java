package shutdown.case2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shutdown.core.ShutDown;

@RestController
public class ShutDownByHandlerController {

    @ShutDown
    @GetMapping("/api/test")
    public ResponseEntity<Void> testApi() {
        return ResponseEntity.ok().build();
    }
}
