package shutdown.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shutdown.core.ShutDownByControllerTest.*;

@AutoConfigureMockMvc
@SpringBootTest
public class ShutDownByControllerTest {

    public static final String API_TO_BE_SHUT_DOWN_BY_BEAN_EXISTS = "/1";
    public static final String API_NOT_TO_BE_SHUT_DOWN = "/2";
    public static final String API_NOT_DEFINED = "/not-defined";
    public static final String API_TO_BE_SHUT_DOWN_BY_MISSING_BEAN = "/3";
    public static final String API_TO_BE_SHUT_DOWN_BY_FORCE = "/4";

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("ShutDown 시 Controller 의 핸들러들이 정의된 응답을 반환한다.")
    @Test
    public void testShutDownApi() throws Exception {
        mockMvc
            .perform(get(API_TO_BE_SHUT_DOWN_BY_BEAN_EXISTS))
            .andExpect(status().isBandwidthLimitExceeded())
            .andExpect(content().string("Not a available api now"));
    }

    @DisplayName("ShutDown 시 Controller Api 외 다른 Api 는 정상 작동한다.")
    @Test
    public void testShutDownApiNotInController() throws Exception {
        mockMvc
            .perform(get(API_NOT_TO_BE_SHUT_DOWN))
            .andExpect(status().isOk());

        mockMvc
            .perform(get(API_NOT_DEFINED))
            .andExpect(status().isNotFound());
    }

    @DisplayName("ConditionOnBean 의 모든 빈이 존재해야 ShutDown 된다.")
    @Test
    public void testShutDownApiByConditionOnBean(@Autowired ApplicationContext context) throws Exception {
        assertThatThrownBy(
            () -> context.getBean(NotRegisteredAsBean.class)
        ).isInstanceOf(BeansException.class);

        mockMvc
            .perform(get(API_NOT_TO_BE_SHUT_DOWN))
            .andExpect(status().isOk());
    }

    @DisplayName("ConditionOnMissingBean 의 모든 빈이 존재하지 않으면 ShutDown 된다.")
    @Test
    public void testShutDownApiByConditionOnMissingBean() throws Exception {
        mockMvc
            .perform(get(API_TO_BE_SHUT_DOWN_BY_MISSING_BEAN))
            .andExpect(status().isServiceUnavailable())
            .andExpect(content().string("Not a available api now"));
    }

    @DisplayName("Force 옵션을 true 로 하면 Condition 과 관계없이 Shut down 된다.")
    @Test
    public void testShutDownApiByForce() throws Exception {
        mockMvc
            .perform(get(API_TO_BE_SHUT_DOWN_BY_FORCE))
            .andExpect(status().isServiceUnavailable())
            .andExpect(content().string("The server is currently unable to handle the request due to a temporary overloading or maintenance of the server."));
    }
}

@ShutDown(
    conditionOnBean = {ShutDownConditionController.class},
    message = "Not a available api now",
    status = HttpStatus.BANDWIDTH_LIMIT_EXCEEDED,
    contentType = MediaType.APPLICATION_JSON_VALUE
)
@RestController
class ShutDownConditionController {

    @GetMapping(value = API_TO_BE_SHUT_DOWN_BY_BEAN_EXISTS)
    public ResponseEntity<Void> api1() {
        return ResponseEntity.ok().build();
    }
}

@ShutDown(
    conditionOnBean = {ShutDownConditionController.class, NotRegisteredAsBean.class},
    message = "Not a available api now",
    status = HttpStatus.BANDWIDTH_LIMIT_EXCEEDED,
    contentType = MediaType.TEXT_EVENT_STREAM_VALUE
)
@RestController
class NotToBeShutDownController {

    @GetMapping(value = API_NOT_TO_BE_SHUT_DOWN)
    public ResponseEntity<Void> api1() {
        return ResponseEntity.ok().build();
    }
}

@ShutDown(
    conditionOnMissingBean = {NotRegisteredAsBean.class},
    message = "Not a available api now"
)
@RestController
class ShutDownConditionOnMissingBean {

    @GetMapping(value = API_TO_BE_SHUT_DOWN_BY_MISSING_BEAN)
    public ResponseEntity<Void> api1() {
        return ResponseEntity.ok().build();
    }
}

@ShutDown(
    force = true
)
@RestController
class ShutDownConditionByForce {

    @GetMapping(value = API_TO_BE_SHUT_DOWN_BY_FORCE)
    public ResponseEntity<Void> api1() {
        return ResponseEntity.ok().build();
    }
}

class NotRegisteredAsBean {

}
