package com.lib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.sprongframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = {"com.lib.*"})
public class LibraryManagementRun {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(LibraryManagementRun.class,args);
	}

}
