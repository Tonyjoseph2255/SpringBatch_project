package com.ust.invoice.extract.exceptions;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(value = HttpMessageNotReadableException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ProblemDetail handleEmptyRequest(HttpMessageNotReadableException exception) {
		log.error("Empty request", exception);
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				"Data is missing in the request or is in inalid format");
		List<String> errors = List.of("Data is missing in the request or is in inalid format");
		return setErrors(problemDetail, errors);
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ProblemDetail handleInvalidRequest(MethodArgumentNotValidException exception) {
		log.error("Invalid data in request", exception);
		ProblemDetail problemDetail = exception.getBody();
		List<String> errors = exception.getFieldErrors().stream().map(fe -> fe.getDefaultMessage()).toList();
		return setErrors(problemDetail, errors);
	}

	@ExceptionHandler(value = InvoiceDataInvalidException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ProblemDetail handleInvalidData(InvoiceDataInvalidException exception) {
		log.error("Invalid data in request", exception);
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
		List<String> errors = exception.getErrors().isEmpty() ? List.of(exception.getMessage()) : exception.getErrors();
		return setErrors(problemDetail, errors);
	}

	private ProblemDetail setErrors(ProblemDetail problemDetail, List<String> errors) {
		problemDetail.setProperties(Map.of("errors", errors));
		return problemDetail;
	}
}
