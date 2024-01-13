package shutdown.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(ShutDownByController.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ShutDownByControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("")
    @Test
    public void case1() throws Exception {
        mockMvc
            .perform(get(ShutDownByController.TO_BE_SHUT_DOWN_PATH_1))
            .andExpect(status().isForbidden())
            .andExpect(content().string("현재 사용할 수 없는 Api 입니다."));
    }
}

@ShutDown(
    conditionOnBean = ShutDownByController.class
)
@RestController
class ShutDownByController {

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
