package com.medhead.emergency;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class for the Emergency Hospital Allocation application.
 * <p>
 * - Marks this as a Spring Boot application.
 * - Enables scheduling support (e.g., for cron jobs).
 */
@SpringBootApplication
@EnableCaching
@PropertySource(value = "classpath:application-secrets.properties", ignoreResourceNotFound = true)
@EnableScheduling  // Enables support for scheduled tasks (e.g., CRON jobs)
public class EmergencyHospitalAllocationApplication {
	@Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
	private String issuer;

	@PostConstruct
	public void debug() {
		System.out.println(">>> OAUTH2 ISSUER = " + issuer);
	}

	/**
	 * Application entry point.
	 * Starts the Spring Boot application context.
	 */
	public static void main(String[] args) {
		SpringApplication.run(EmergencyHospitalAllocationApplication.class, args);
	}


}
