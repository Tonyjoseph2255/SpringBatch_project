package com.ust.invoice.extract.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ust.invoice.extract.entity.InvoiceConfig;

public interface InvoiceConfigRepository extends JpaRepository<InvoiceConfig, Long> {

	Optional<InvoiceConfig> findByInvoicePath(String invoicePath);
}
