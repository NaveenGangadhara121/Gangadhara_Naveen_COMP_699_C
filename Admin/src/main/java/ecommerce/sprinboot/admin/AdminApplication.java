package ecommerce.sprinboot.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"ecommerce.sprinboot.library", "ecommerce.sprinboot.admin"})
@EnableJpaRepositories(basePackages = "ecommerce.sprinboot.library.repository")
@EntityScan(basePackages = "ecommerce.sprinboot.library.model")
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
