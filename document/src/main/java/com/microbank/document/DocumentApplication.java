package com.microbank.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.microbank.document.feign")
public class DocumentApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentApplication.class, args);
	}

}
