package com.medhead.emergency;

import org.springframework.boot.SpringApplication;

public class TestEmergencyHospitalAllocationApplication {

	public static void main(String[] args) {
		SpringApplication.from(EmergencyHospitalAllocationApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
