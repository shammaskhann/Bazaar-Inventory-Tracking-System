package com.example.internshipproject.InventoryManagementV2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class InventoryManagementV2Application {

	public static void main(String[] args) {
		SpringApplication.run(InventoryManagementV2Application.class, args);
	}

}
