package com.medhead.emergency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * Main class for the Emergency Hospital Allocation application.
 * <p>
 * - Marks this as a Spring Boot application.
 * - Enables scheduling support (e.g., for cron jobs).
 */
@SpringBootApplication
@EnableCaching
@PropertySource("classpath:application-secrets.properties")
@EnableScheduling  // Enables support for scheduled tasks (e.g., CRON jobs)
public class EmergencyHospitalAllocationApplication {

	/**
	 * Application entry point.
	 * Starts the Spring Boot application context.
	 */
	public static void main(String[] args) {
		SpringApplication.run(EmergencyHospitalAllocationApplication.class, args);
	}

}
