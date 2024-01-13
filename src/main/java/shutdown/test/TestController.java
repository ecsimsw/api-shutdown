package shutdown.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shutdown.core.ShutDown;

@ShutDown(
    message = "ShutDown",
    status = HttpStatus.SERVICE_UNAVAILABLE,
    force = true
)
@RestController
public class TestController {

    @GetMapping(value = "/api/hi", consumes = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<String> hi() {
        return ResponseEntity.ok("hey");
    }
}
