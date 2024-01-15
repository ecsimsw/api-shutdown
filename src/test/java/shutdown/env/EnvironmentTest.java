package shutdown.env;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shutdown.SharedConfigurationReference;
import shutdown.ShutDownCorePackageContext;
import shutdown.core.ShutDownByControllerTest;
import shutdown.core.ShutDownFilterRegister;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("테스트 환경을 확인한다.")
@EnableAutoConfiguration
@Import(TestEnvController.class)
@SpringBootTest(classes = ShutDownCorePackageContext.class)
public class EnvironmentTest {

    @Autowired
    ApplicationContext applicationContext;

    @DisplayName("라이브러리의 shutdown.core 패키지 외에는 Test context 에서 제외한다.")
    @Test
    public void checkComponentScanRange() {
        assertThatThrownBy(
            () -> applicationContext.getBean(SharedConfigurationReference.class)
        ).isInstanceOf(NoSuchBeanDefinitionException.class);

        var beanInCorePackage = applicationContext.getBean(ShutDownFilterRegister.class);
        assertThat(beanInCorePackage).isNotNull();
    }

    @DisplayName("Test context 는 패키지마다 격리되어야 한다.")
    @Test
    public void checkTestCasePackageIsolated() {
        assertThatThrownBy(
            () -> applicationContext.getBean(ShutDownByControllerTest.class)
        ).isInstanceOf(NoSuchBeanDefinitionException.class);

        var beanInTestEnvPackage = applicationContext.getBean(TestEnvController.class);
        assertThat(beanInTestEnvPackage).isNotNull();
    }
}

@RestController
class TestEnvController {

    @GetMapping("/api/test")
    public ResponseEntity<String> testApi() {
        return ResponseEntity.ok("test");
    }
}
