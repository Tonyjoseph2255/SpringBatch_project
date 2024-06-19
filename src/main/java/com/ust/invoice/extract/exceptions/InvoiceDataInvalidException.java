package com.ust.invoice.extract.exceptions;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class InvoiceDataInvalidException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> errors = new ArrayList<>();

	public InvoiceDataInvalidException() {
		this("Invalid input");
	}

	public InvoiceDataInvalidException(String message) {
		super(message);
	}

	public InvoiceDataInvalidException(String message, List<String> errors) {
		this(message);
		this.errors = errors;
	}

	public InvoiceDataInvalidException(List<String> errors) {
		this();
		this.errors = errors;
	}

}
