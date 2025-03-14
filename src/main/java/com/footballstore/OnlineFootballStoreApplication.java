package com.footballstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OnlineFootballStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineFootballStoreApplication.class, args);
	}

}
