package com.ust.invoice.extract.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.ust.invoice.extract.entity.InvoiceConfig;
import com.ust.invoice.extract.enums.ScheduleType;
import com.ust.invoice.extract.repository.InvoiceConfigRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DynamicSchedulingService {

	@Autowired
	private InvoiceConfigRepository invoiceConfigRepository;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("invoiceExtractJob")
	private Job job;

	@Autowired
	private TaskScheduler taskScheduler;

	public void scheduleJobs() {
		log.info("Scheduling Jobs");
		List<InvoiceConfig> invoiceConfigs = invoiceConfigRepository.findAll();
		log.info("Scheduling for {} jobs", invoiceConfigs.size());
		for (InvoiceConfig invoiceConfig : invoiceConfigs) {
			log.info("Scheduling for Config ID {}", invoiceConfig.getId());
			String cronExpression = generateCronExpression(invoiceConfig);
//			String cronExpression = "0 * * * * ?";
			taskScheduler.schedule(() -> launchJob(),
					new CronTrigger(cronExpression, TimeZone.getTimeZone(ZoneId.systemDefault())));
		}
	}

	private String generateCronExpression(InvoiceConfig invoiceConfig) {
		String cronExpression = null;
		if (ScheduleType.DAILY.equals(invoiceConfig.getScheduleType())) {
			LocalDateTime startTime = LocalDateTime.now().with(invoiceConfig.getStartTime());
			cronExpression = String.format("0 %d %d * * ?", startTime.getMinute(), startTime.getHour());
		} else if (ScheduleType.WEEKLY.equals(invoiceConfig.getScheduleType())) {
			cronExpression = generateWeeklyCronExpression(invoiceConfig);
		} else if (ScheduleType.MONTHLY.equals(invoiceConfig.getScheduleType())) {
			cronExpression = generateMonthlyCronExpression(invoiceConfig);
		}
		return cronExpression;
	}

	private String generateWeeklyCronExpression(InvoiceConfig invoiceConfig) {
		String[] days = invoiceConfig.getDaysOfWeek().split(",");
		LocalDateTime startTime = LocalDateTime.now().with(invoiceConfig.getStartTime());
		StringBuilder cron = new StringBuilder();
		for (String day : days) {
			cron.append(String.format("0 %d %d ? * %s,", startTime.getMinute(), startTime.getHour(),
					day.substring(0, 3).toUpperCase()));
		}
		cron.deleteCharAt(cron.length() - 1); // Remove last comma
		return cron.toString();
	}

	private String generateMonthlyCronExpression(InvoiceConfig invoiceConfig) {
		LocalDateTime startTime = LocalDateTime.now().with(invoiceConfig.getStartTime());
		StringBuilder cron = new StringBuilder();

		if (invoiceConfig.getSpecificDaysOfMonth() != null && !invoiceConfig.getSpecificDaysOfMonth().isEmpty()) {
			cron.append(String.format("0 %d %d %s * ?", startTime.getMinute(), startTime.getHour(),
					invoiceConfig.getSpecificDaysOfMonth()));
		} else {
			cron.append(String.format("0 %d %d 1 * ?", startTime.getMinute(), startTime.getHour()));
		}

		return cron.toString();
	}

	private void launchJob() {
		try {
			JobExecution execution = jobLauncher.run(job,
					new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
			log.info("Job executed with status: {}", execution.getStatus());
		} catch (Exception e) {
			log.error("Job execution failed", e);
		}
	}
}
