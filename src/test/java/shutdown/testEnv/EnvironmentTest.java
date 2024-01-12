package shutdown.testEnv;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import shutdown.SharedConfigurationReference;
import shutdown.case1.ShutDownByController;
import shutdown.case2.ShutDownByHandlerController;
import shutdown.core.ShutDownFilterRegister;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("테스트 환경을 확인한다.")
@Import(TestEnvController.class)
@SpringBootTest
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

    @DisplayName("Test context 는 test 의 case 패키지마다 격리되어야 한다.")
    @Test
    public void checkTestCasePackageIsolated() {
        assertThatThrownBy(
            () -> applicationContext.getBean(ShutDownByController.class)
        ).isInstanceOf(NoSuchBeanDefinitionException.class);

        assertThatThrownBy(
            () -> applicationContext.getBean(ShutDownByHandlerController.class)
        ).isInstanceOf(NoSuchBeanDefinitionException.class);

        var beanInTestEnvPackage = applicationContext.getBean(TestEnvController.class);
        assertThat(beanInTestEnvPackage).isNotNull();
    }
}
