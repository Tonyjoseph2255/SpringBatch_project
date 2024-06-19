package com.ust.invoice.extract.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import com.ust.invoice.extract.tasklet.InvoiceExtractionTasklet;

import lombok.AllArgsConstructor;

@Configuration
@EnableScheduling
@AllArgsConstructor
public class BatchConfig {

	private JobRepository jobRepository;

	private InvoiceExtractionTasklet invoiceExtractionTasklet;

	private PlatformTransactionManager transactionManager;

	@Bean("asyncJobLauncher")
	JobLauncher asyncJobLauncher() throws Exception {
		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	@Bean("invoiceExtractStep")
	Step invoiceExtractStep() {
		return new StepBuilder("invoiceExtractStep", jobRepository)
				.tasklet(invoiceExtractionTasklet, transactionManager).allowStartIfComplete(true).build();

	}

	@Bean("invoiceExtractJob")
	Job invoiceExtractJob() {
		return new JobBuilder("invoiceExtractJob", jobRepository).incrementer(new RunIdIncrementer())
				.start(invoiceExtractStep()).build();
	}
}
