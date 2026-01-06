package com.eob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EveryoneBreadApplication {

	public static void main(String[] args) {
		SpringApplication.run(EveryoneBreadApplication.class, args);
	}

}
