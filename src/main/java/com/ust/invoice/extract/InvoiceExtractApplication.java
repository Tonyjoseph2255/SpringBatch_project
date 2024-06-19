package com.ust.invoice.extract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InvoiceExtractApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceExtractApplication.class, args);
	}

}
