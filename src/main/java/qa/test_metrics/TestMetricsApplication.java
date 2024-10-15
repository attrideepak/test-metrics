package qa.test_metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "qa/test_metrics/repository")
public class TestMetricsApplication {

	public static void main(String[] args) {

		SpringApplication.run(TestMetricsApplication.class, args);
		System.out.println("Hello World");
	}

}
