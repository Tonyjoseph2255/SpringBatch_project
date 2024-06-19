package com.ust.invoice.extract.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ust.invoice.extract.entity.InvoiceConfig;
import com.ust.invoice.extract.exceptions.InvoiceDataInvalidException;
import com.ust.invoice.extract.payload.InvoiceConfigDTO;
import com.ust.invoice.extract.repository.InvoiceConfigRepository;
import com.ust.invoice.extract.util.DateTimeUtil;
import com.ust.invoice.extract.validate.InputValidator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class InvoiceConfigService {

	private InputValidator inputValidator;

	private InvoiceConfigRepository invoiceConfigRepository;

//	private AsyncJobService asyncJobService;

	private DynamicSchedulingService dynamicSchedulingService;

	@Transactional
	public void saveConfig(InvoiceConfigDTO invoiceConfigDTO) {
		inputValidator.validate(invoiceConfigDTO);

		if (invoiceConfigRepository.findByInvoicePath(invoiceConfigDTO.getInvoicePath()).isPresent()) {
			throw new InvoiceDataInvalidException("Given invoice path is already saved");
		}
		InvoiceConfig invoiceConfig = InvoiceConfig.builder().invoicePath(invoiceConfigDTO.getInvoicePath())
				.archivePath(invoiceConfigDTO.getArchivePath()).excelPath(invoiceConfigDTO.getExcelPath())
				.scheduleType(invoiceConfigDTO.getScheduleType()).daysOfWeek(invoiceConfigDTO.getDaysOfWeek())
				.startTime(DateTimeUtil.getLocalTime(invoiceConfigDTO.getStartTime()))
				.specificDaysOfMonth(invoiceConfigDTO.getSpecificDaysOfMonth()).build();
		invoiceConfig = invoiceConfigRepository.save(invoiceConfig);
		log.info("Invoice Configuration is saved with id {}", invoiceConfig.getId());
		scheduleAndRunJob();
	}

	@Transactional
	public void updateConfig(InvoiceConfigDTO invoiceConfigDTO) {
		inputValidator.validate(invoiceConfigDTO);
		Long id = invoiceConfigDTO.getId();
		InvoiceConfig invoiceConfig = invoiceConfigRepository.findById(id).orElse(new InvoiceConfig());
		if (id == null && invoiceConfigRepository.findByInvoicePath(invoiceConfigDTO.getInvoicePath()).isPresent()) {
			throw new InvoiceDataInvalidException("Given invoice path is already saved");
		}

		invoiceConfig.setArchivePath(invoiceConfigDTO.getArchivePath());
		invoiceConfig.setInvoicePath(invoiceConfigDTO.getInvoicePath());
		invoiceConfig.setExcelPath(invoiceConfigDTO.getExcelPath());
		invoiceConfig.setScheduleType(invoiceConfigDTO.getScheduleType());
		invoiceConfig.setDaysOfWeek(invoiceConfigDTO.getDaysOfWeek());
		invoiceConfig.setStartTime(DateTimeUtil.getLocalTime(invoiceConfigDTO.getStartTime()));
		invoiceConfig.setSpecificDaysOfMonth(invoiceConfigDTO.getSpecificDaysOfMonth());
		invoiceConfig = invoiceConfigRepository.save(invoiceConfig);
		log.info("Invoice Configuration is updated for id {}", invoiceConfig.getId());
		scheduleAndRunJob();
	}

	public InvoiceConfigDTO getConfig() {
		List<InvoiceConfig> configs = invoiceConfigRepository.findAll();
		InvoiceConfigDTO invoiceConfigDTO = null;
		if (!configs.isEmpty()) {
			InvoiceConfig config = configs.get(0);
			invoiceConfigDTO = InvoiceConfigDTO.builder().id(config.getId()).invoicePath(config.getInvoicePath())
					.excelPath(config.getExcelPath()).archivePath(config.getArchivePath())
					.scheduleType(config.getScheduleType()).daysOfWeek(config.getDaysOfWeek())
					.startTime(DateTimeUtil.toTimeString(config.getStartTime()))
					.specificDaysOfMonth(config.getSpecificDaysOfMonth()).build();
		}
		return invoiceConfigDTO;
	}

	public List<InvoiceConfig> getAllConfigs() {
		return invoiceConfigRepository.findAll();
	}

	private void scheduleAndRunJob() {
		log.info("Scheduling the jobs with saved data");
		dynamicSchedulingService.scheduleJobs();
//		asyncJobService.runJob();
	}
}
