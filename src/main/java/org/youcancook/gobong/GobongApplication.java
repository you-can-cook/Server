package org.youcancook.gobong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GobongApplication {

	public static void main(String[] args) {
		SpringApplication.run(GobongApplication.class, args);
	}

}
