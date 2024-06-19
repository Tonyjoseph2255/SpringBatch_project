package com.ust.invoice.extract.controller;

import com.ust.invoice.extract.entity.InvoiceExecutionStatus;
import com.ust.invoice.extract.entity.JobStatusResponse;
import com.ust.invoice.extract.service.InvoiceMonitoringService;
import com.ust.invoice.extract.util.DateTimeUtil;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ust.invoice.extract.service.AsyncJobService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
@Slf4j
@AllArgsConstructor
public class ExtratorTriggerController {
	@Autowired
	private JobExplorer jobExplorer;


	private AsyncJobService asyncJobService;
	private final InvoiceMonitoringService invoiceMonitoringService;

	@GetMapping("start")
	public ResponseEntity<String> saveConfig() {
		log.info("Starting Job");
		asyncJobService.runJob();
		log.info("Job started succesfully");
		return ResponseEntity.ok("Job is started");
	}
	@GetMapping("/executions/statuses")
	public List<InvoiceExecutionStatus> getAllJobExecutionStatuses() {
		return invoiceMonitoringService.getAllJobExecutionStatuses();
	}
	@GetMapping("/executions/failed")
	public List<InvoiceExecutionStatus> getFailedJobExecutionStatuses() {
		return invoiceMonitoringService.getFailedJobExecutionStatuses();
	}
	@GetMapping("/jobStatus")
	public ResponseEntity<List<JobStatusResponse>> getLast10JobStatuses() {
		String jobName = "invoiceExtractJob";
		List<JobStatusResponse> jobStatusResponses = invoiceMonitoringService.getLast10JobStatuses(jobName);
		return ResponseEntity.ok(jobStatusResponses);
	}


}
