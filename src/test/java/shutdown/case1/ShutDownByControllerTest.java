package shutdown.case1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shutdown.testEnv.TestEnvController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller 에 ShutDown 이 명시된 경우")
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
