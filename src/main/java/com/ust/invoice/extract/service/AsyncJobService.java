package com.ust.invoice.extract.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AsyncJobService {

	@Autowired
	@Qualifier("asyncJobLauncher")
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("invoiceExtractJob")
	private Job job;

	@Async
	public void runJob() {
		try {
			log.info("Running batch Job");
			JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis())
					.toJobParameters();
			jobLauncher.run(job, jobParameters);
//			CompletableFuture.runAsync(() -> {
//				try {
//					// You can add parameters to the job execution if needed
//					JobExecution execution = jobLauncher.run(job, new JobParameters());
//					log.info("Job completed successfully: {} ", execution.getStatus());
//				} catch (Exception e) {
//					log.info("Job failed with error: ", e);
//				}
//			});
		} catch (Exception e) {
			log.error("Error in executing batch job", e);
		}
	}
}
