package com.miniproject.rookiejangter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RookiejangterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RookiejangterApplication.class, args);
	}

}
