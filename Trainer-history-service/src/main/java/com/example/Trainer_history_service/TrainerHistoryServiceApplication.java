package com.example.Trainer_history_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class TrainerHistoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainerHistoryServiceApplication.class, args);
	}

}
