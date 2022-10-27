package jub.diogen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class DiogenApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiogenApplication.class, args);
	}

}
