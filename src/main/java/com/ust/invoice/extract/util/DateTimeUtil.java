package com.ust.invoice.extract.util;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTimeUtil {

	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final String HH_MM_A = "hh:mm a";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DD_MM_YYYY);
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(HH_MM_A);

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DD_MM_YYYY + " " + HH_MM_A);
	private DateTimeUtil() {
	}

	public static LocalDate getDate(String input) {
		LocalDate localDate = null;
		try {
			localDate = LocalDate.parse(input, DATE_FORMATTER);
		} catch (DateTimeParseException e) {
			log.error("Error in input data", e);
		}
		return localDate;
	}

	public static String toDateString(LocalDate localDate) {
		return DATE_FORMATTER.format(localDate);
	}

	public static LocalTime getTime(String input) {
		LocalTime localTime = null;
		try {
			localTime = getLocalTime(input);
		} catch (DateTimeParseException e) {
			log.error("Error in input data", e);
		}
		return localTime;
	}

	public static String getDateTime(LocalDateTime localDateTime) {
		if(localDateTime!=null) {
			return DATE_TIME_FORMATTER.format(localDateTime);
		}
		return null;
	}

	public static LocalTime getLocalTime(String input) {
		return LocalTime.parse(input, TIME_FORMATTER);
	}

	public static String toTimeString(LocalTime input) {
		return TIME_FORMATTER.format(input);
	}
}