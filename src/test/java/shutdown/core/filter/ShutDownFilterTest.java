package shutdown.core.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import shutdown.core.filter.testEnv.NotRegisteredAsBean;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shutdown.core.filter.testEnv.TestApis.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureMockMvc
@SpringBootTest(classes = ShutDownFilterPackageContext.class)
public class ShutDownFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("ShutDown 시 정의된 재난 응답을 반환한다.")
    @Test
    public void testShutDownApi() throws Exception {
        mockMvc
            .perform(get(API_TO_BE_SHUTDOWN))
            .andExpect(status().isAlreadyReported())
            .andExpect(content().string("API_TO_BE_SHUTDOWN"));
    }

    @DisplayName("여러 Mapping 정보를 포함한 핸들러를 처리할 수 있다.")
    @Test
    public void testShutDownApiWithMultipleMappings() throws Exception {
        mockMvc
            .perform(get(API_TO_BE_SHUTDOWN_MULTIPLE_MAPPINGS_1))
            .andExpect(status().isServiceUnavailable());
        mockMvc
            .perform(get(API_TO_BE_SHUTDOWN_MULTIPLE_MAPPINGS_2))
            .andExpect(status().isServiceUnavailable());
        mockMvc
            .perform(post(API_TO_BE_SHUTDOWN_MULTIPLE_MAPPINGS_1))
            .andExpect(status().isServiceUnavailable());
        mockMvc
            .perform(post(API_TO_BE_SHUTDOWN_MULTIPLE_MAPPINGS_2))
            .andExpect(status().isServiceUnavailable());
    }

    @DisplayName("ShutDown 시 Controller Api 외 다른 Api 는 정상 작동한다.")
    @Test
    public void testShutDownApiNotInController() throws Exception {
        mockMvc
            .perform(get(API_NOT_TO_BE_SHUTDOWN_BY_BEAN_EXISTS))
            .andExpect(status().isOk());
        mockMvc
            .perform(get(API_NOT_DEFINED))
            .andExpect(status().isNotFound());
    }

    @DisplayName("ConditionOnBean 으로 ShutDown 의 조건을 빈의 여부로 할 수 있다")
    @Test
    public void testShutDownApiByConditionOnBean(@Autowired ApplicationContext context) throws Exception {
        mockMvc
            .perform(get(API_NOT_TO_BE_SHUTDOWN_BY_BEAN_EXISTS))
            .andExpect(status().isOk());
    }

    @DisplayName("ConditionOnBean 의 모든 빈이 존재해야 ShutDown 된다.")
    @Test
    public void testShutDownApiByConditionOnBeanMiss(@Autowired ApplicationContext context) throws Exception {
        assertThatThrownBy(
            () -> context.getBean(NotRegisteredAsBean.class)
        ).isInstanceOf(BeansException.class);

        mockMvc
            .perform(get(API_NOT_TO_BE_SHUTDOWN_BY_BEAN_EXISTS))
            .andExpect(status().isOk());
    }

    @DisplayName("ConditionOnMissingBean 의 모든 빈이 존재하지 않으면 ShutDown 된다.")
    @Test
    public void testShutDownApiByConditionOnMissingBean() throws Exception {
        mockMvc
            .perform(get(API_TO_BE_SHUTDOWN_BY_MISSING_BEAN))
            .andExpect(status().isServiceUnavailable());
    }

    @DisplayName("Force 옵션을 true 로 하면 Condition 과 관계없이 Shut down 된다.")
    @Test
    public void testShutDownApiByForce() throws Exception {
        mockMvc
            .perform(get(API_TO_BE_SHUTDOWN_BY_FORCE))
            .andExpect(status().isServiceUnavailable());
    }
}
