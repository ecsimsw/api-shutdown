package shutdown.case2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import shutdown.SharedConfigurationReference;
import shutdown.core.ShutDownHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class ShutDownByHandlerTest {

    @Autowired
    ApplicationContext applicationContext;

    @DisplayName("")
    @Test
    public void case1() {
        assertThatThrownBy(
            () -> applicationContext.getBean(SharedConfigurationReference.class)
        ).isInstanceOf(NoSuchBeanDefinitionException.class);

        var bean = applicationContext.getBean(ShutDownHandler.class);
        assertThat(bean).isNotNull();
    }
}
