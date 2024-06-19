package com.ust.invoice.extract.service;

import java.time.Month;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ust.invoice.extract.entity.InvoiceData;
import com.ust.invoice.extract.exceptions.InvoiceDataInvalidException;
import com.ust.invoice.extract.repository.InvoiceDataRepository;
import com.ust.invoice.extract.serdeser.FieldAliasMapConverter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InvoiceDataService {

	@Autowired
	private InvoiceDataRepository invoiceDataRepository;

	public List<Map<String, String>> getInvoiceDataAll() {
		List<InvoiceData> invoiceDataList = invoiceDataRepository.findAll();
		log.info("Got {} items", invoiceDataList.size());
		return invoiceDataList.stream().map(FieldAliasMapConverter::objectToMap).toList();
	}

	public List<Map<String, String>> searchInvoiceData(String yearMonth) {
		if (!yearMonth.matches("\\d{4}-[a-zA-Z]+")) {
			throw new InvoiceDataInvalidException("Invalid format. Expected YYYY-MONTH");
		}
		String[] parts = yearMonth.split("-");
		int year = Integer.parseInt(parts[0].trim().strip());
		Month month = getMonth(parts);
		List<InvoiceData> invoiceDataList = invoiceDataRepository.getByMonthAndYear(year, month.getValue());
		log.info("Got {} items", invoiceDataList.size());
		return invoiceDataList.stream().map(FieldAliasMapConverter::objectToMap).toList();
	}

	private Month getMonth(String[] parts) {
		String monthString = parts[1].trim().strip().toUpperCase();
		Month month = null;
		try {
			month = Month.valueOf(monthString);
		} catch (Exception e) {
			throw new InvoiceDataInvalidException("Invalid month");
		}
		if (month == null) {
			throw new InvoiceDataInvalidException("Invalid month");
		}
		return month;
	}
}
