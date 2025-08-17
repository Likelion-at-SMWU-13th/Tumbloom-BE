package com.tumbloom.tumblerin;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TumblerinApplication {

	public static void main(String[] args) {
		SpringApplication.run(TumblerinApplication.class, args);
	}

	@PostConstruct
	public void checkDb() {
		System.out.println("Active profile: " + System.getProperty("spring.profiles.active"));
		System.out.println("DB_JDBC_URL: " + System.getenv("DB_JDBC_URL"));
	}

}
