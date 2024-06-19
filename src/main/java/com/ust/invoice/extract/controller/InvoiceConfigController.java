package com.ust.invoice.extract.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ust.invoice.extract.payload.InvoiceConfigDTO;
import com.ust.invoice.extract.service.InvoiceConfigService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("config")
@CrossOrigin(origins = "*")
@Slf4j
@AllArgsConstructor
public class InvoiceConfigController {

	private InvoiceConfigService invoiceConfigService;

	@PostMapping
	public ResponseEntity<String> saveConfig(@RequestBody @Valid InvoiceConfigDTO invoiceConfigDTO) {
		log.info("Save Invoice Configuration");
		invoiceConfigService.saveConfig(invoiceConfigDTO);
		log.info("Returning status");
		return ResponseEntity.ok("Configuration saved Succesfully");
	}

	@GetMapping
	public ResponseEntity<InvoiceConfigDTO> getConfig() {
		log.info("Getting configurarion");
		return ResponseEntity.ok(invoiceConfigService.getConfig());
	}

	@PutMapping
	public ResponseEntity<String> updateConfig(@RequestBody @Valid InvoiceConfigDTO invoiceConfigDTO) {
		log.info("Updating Configuration");
		invoiceConfigService.updateConfig(invoiceConfigDTO);
		return ResponseEntity.ok("Configuration saved Succesfully");
	}

}
