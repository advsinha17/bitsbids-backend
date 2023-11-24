package com.bitsbids.bitsbids;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BitsbidsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BitsbidsApplication.class, args);
	}
}
