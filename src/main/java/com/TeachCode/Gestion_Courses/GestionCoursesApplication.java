package com.TeachCode.Gestion_Courses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GestionCoursesApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionCoursesApplication.class, args);
	}

}
