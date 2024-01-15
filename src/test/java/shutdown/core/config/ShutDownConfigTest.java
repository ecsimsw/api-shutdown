package shutdown.core.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shutdown.core.config.testEnv.TestApis.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@SpringBootTest(classes = ShutDownConfigPackageContext.class)
public class ShutDownConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("ShutDown 시 Global config 에 정의된 응답을 반환한다.")
    @Test
    public void testShutDownApi() throws Exception {
        mockMvc
            .perform(get(API_WITHOUT_LOCAL_SHUTDOWN_CONFIG))
            .andExpect(status().is(GLOBAL_STATUS.value()))
            .andExpect(content().string(GLOBAL_MESSAGE));
    }

    @DisplayName("ShutDown 어노테이션에 직접 정의된 응답 정보가 있다면 이를 우선으로 한다.")
    @Test
    public void testShutDownApiLocalConfig() throws Exception {
        mockMvc
            .perform(get(API_WITH_LOCAL_SHUTDOWN_CONFIG))
            .andExpect(content().string(CUSTOM_MESSAGE));
    }
}
