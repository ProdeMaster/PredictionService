package com.ProdeMaster.PredictionService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PredictionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PredictionServiceApplication.class, args);
	}

}
