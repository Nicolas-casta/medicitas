package com.cesde.medicitas;

import org.springframework.boot.SpringApplication;

public class TestMedicitasApplication {

	public static void main(String[] args) {
		SpringApplication.from(MedicitasApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
