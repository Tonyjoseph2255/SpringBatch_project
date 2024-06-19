package com.ust.invoice.extract.payload;

import com.ust.invoice.extract.enums.ScheduleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceConfigDTO {

	private Long id;

	@NotBlank(message = "Invoice Path shall not be blank")
	private String invoicePath;

	@NotBlank(message = "Excel output path shall not be blank")
	private String excelPath;

	@NotBlank(message = "Archive path shall not be blank")
	private String archivePath;

	@NotNull(message = "Scheduling type shall not be empty")
	private ScheduleType scheduleType;

	@NotBlank(message = "Scheduling time shall not be empty")
	private String startTime;

	private String daysOfWeek;

	private String specificDaysOfMonth;

}
