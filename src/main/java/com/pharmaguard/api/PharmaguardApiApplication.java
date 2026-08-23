package com.pharmaguard.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PharmaguardApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PharmaguardApiApplication.class, args);
	}

}
