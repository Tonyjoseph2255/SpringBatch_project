package com.ust.invoice.extract.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ust.invoice.extract.service.InvoiceDataService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
@Slf4j
@AllArgsConstructor
public class InvoiceDataController {

	private InvoiceDataService invoiceDataService;

	@GetMapping("data-all")
	public ResponseEntity<List<Map<String, String>>> getInvoiceData() {
		log.info("Getting all invoice data");
		return ResponseEntity.ok(invoiceDataService.getInvoiceDataAll());
	}

	@GetMapping("data")
	public ResponseEntity<List<Map<String, String>>> searchInvoiceData(@RequestParam String yearMonth) {
		log.info("Getting invoice data for {}", yearMonth);
		return ResponseEntity.ok(invoiceDataService.searchInvoiceData(yearMonth));
	}
}
