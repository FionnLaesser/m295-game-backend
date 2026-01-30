package ch.wiss.m295;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ch.wiss.m295")
public class M295GameBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(M295GameBackendApplication.class, args);
    }
}
