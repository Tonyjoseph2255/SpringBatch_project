package com.ust.invoice.extract.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ust.invoice.extract.enums.ScheduleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Invoice Path shall not be blank")
	@Column(nullable = false)
	private String invoicePath;

	@NotBlank(message = "Excel output path shall not be blank")
	@Column(nullable = false)
	private String excelPath;

	@NotBlank(message = "Archive path shall not be blank")
	@Column(nullable = false)
	private String archivePath;

	@NotNull(message = "Scheduling type shall not be blank")
	@Column
	@Enumerated(EnumType.STRING)
	private ScheduleType scheduleType;

	@NotNull(message = "Start time shall not be blank")
	@Column
	private LocalTime startTime;
	
	@Column
	private String daysOfWeek;

	@Column
	private String specificDaysOfMonth;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime modifiedAt;
}
