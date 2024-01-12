package shutdown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class SpringMainApplication {

    public static void main(String[] args) {
        var application = new SpringApplication(SpringMainApplication.class);
        application.run(args);
    }
}
