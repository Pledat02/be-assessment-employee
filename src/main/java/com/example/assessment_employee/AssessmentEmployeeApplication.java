package com.example.assessment_employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

@SpringBootApplication
@EnableFeignClients
public class AssessmentEmployeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssessmentEmployeeApplication.class, args);
	}

}
