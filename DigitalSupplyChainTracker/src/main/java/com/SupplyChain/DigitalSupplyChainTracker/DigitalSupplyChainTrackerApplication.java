package com.SupplyChain.DigitalSupplyChainTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DigitalSupplyChainTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalSupplyChainTrackerApplication.class, args);
	}

}
