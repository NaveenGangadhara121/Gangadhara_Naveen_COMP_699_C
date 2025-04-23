package ecommerce.sprinboot.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"ecommerce.sprinboot.customer", "ecommerce.sprinboot.library"})
@EnableJpaRepositories(basePackages = "ecommerce.sprinboot.library.repository")
@EntityScan(basePackages = "ecommerce.sprinboot.library.model")
public class CustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }
}
