package com.ust.invoice.extract.validate;

import java.io.File;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ust.invoice.extract.enums.ScheduleType;
import com.ust.invoice.extract.enums.WeekDays;
import com.ust.invoice.extract.exceptions.InvoiceDataInvalidException;
import com.ust.invoice.extract.payload.InvoiceConfigDTO;
import com.ust.invoice.extract.util.DateTimeUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class InputValidator {

	public void validate(InvoiceConfigDTO invoiceConfigDTO) {

		List<String> errors = new ArrayList<>();
		String invoicePath = invoiceConfigDTO.getInvoicePath();
		validateIfDirectoryAndExists(invoicePath, errors);
		String archivePath = invoiceConfigDTO.getArchivePath();
		validatePath(archivePath, errors);
		validateExcelFile(invoiceConfigDTO.getExcelPath(), errors);
		if (invoicePath.equals(archivePath)) {
			errors.add("Input path and archive path are same");
		}
		validateScheduleConfig(invoiceConfigDTO, errors);

		if (!errors.isEmpty()) {
			log.warn("Found {} invalid input data", errors.size());
			throw new InvoiceDataInvalidException("Invalid Input", errors);
		}
		log.info("Validation completed");
	}

	private void validateScheduleConfig(InvoiceConfigDTO invoiceConfigDTO, List<String> errors) {
		ScheduleType scheduleType = invoiceConfigDTO.getScheduleType();
		validateStartTime(invoiceConfigDTO, errors);
		switch (scheduleType) {
		case WEEKLY:
			String daysOfWeek = invoiceConfigDTO.getDaysOfWeek();
			if (StringUtils.isBlank(daysOfWeek)) {
				errors.add("Days of week shall not empty for Weekly schedules");
			}
			List<String> weekDayList = Arrays.asList(WeekDays.values()).stream().map(day -> day.name()).toList();
			boolean validDays = Arrays.stream(daysOfWeek.split(",")).allMatch(day -> weekDayList.contains(day));
			if (!validDays) {
				errors.add("Invalid Week days");
			}
			break;
		case MONTHLY:
			validateSpecificDaysOfMonth(invoiceConfigDTO, errors);
			break;
		default:
			break;
		}

	}

	private void validateStartTime(InvoiceConfigDTO invoiceConfigDTO, List<String> errors) {
		String startTime = invoiceConfigDTO.getStartTime();
		try {
			DateTimeUtil.getLocalTime(startTime);
		} catch (DateTimeParseException e) {
			errors.add("Invalid Start time");
		}
	}

	private void validateSpecificDaysOfMonth(InvoiceConfigDTO invoiceConfigDTO, List<String> errors) {
		String specificDaysOfMonth = invoiceConfigDTO.getSpecificDaysOfMonth();
		if (StringUtils.isNotBlank(specificDaysOfMonth)) {
			try {
				boolean anyMatch = Arrays.stream(specificDaysOfMonth.split(",")).mapToInt(day -> Integer.valueOf(day))
						.anyMatch(day -> day > 31);
				if (anyMatch) {
					errors.add("Invalid Days");
				}
			} catch (Exception e) {
				errors.add("Invalid Days");
			}
		}
	}

	private void validatePath(String inputPath, List<String> errors) {
		try {
			Paths.get(inputPath);
		} catch (Exception e) {
			errors.add(inputPath + " is not a valid path");
		}
	}

	private void validateIfDirectoryAndExists(String inputPath, List<String> errors) {
		validatePath(inputPath, errors);
		File file = new File(inputPath);
		if (!file.isDirectory()) {
			errors.add(inputPath + " is not a valid directory");
		}
		if (!file.exists()) {
			errors.add(inputPath + " does not exists");
		}
	}

	private void validateExcelFile(String excelPath, List<String> errors) {
		validatePath(excelPath, errors);
		File file = new File(excelPath);
		String message = excelPath + " is not a valid excel path";
		if ((!excelPath.endsWith(".xlsx") && !excelPath.endsWith(".xls")) || file.isDirectory()) {
			errors.add(message);
		}
		File parentDirectory = file.getParentFile();
		if (!parentDirectory.exists() || !parentDirectory.isDirectory()) {
			errors.add(message);
		}
	}

}
