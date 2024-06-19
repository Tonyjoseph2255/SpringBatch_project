package com.ust.invoice.extract.runner;

import com.ust.invoice.extract.service.DynamicSchedulingService;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BatchAppRunner implements ApplicationRunner {

    @Autowired
    private DynamicSchedulingService dynamicSchedulingService;

    @Autowired
    private JobExplorer jobExplorer;
    @Autowired
    private JobRepository jobRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        markRunningJobsAsAbandoned();
        dynamicSchedulingService.scheduleJobs();
    }

    private void markRunningJobsAsAbandoned() {
        List<String> jobNames = jobExplorer.getJobNames();
        for (String jobName : jobNames) {
            Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions(jobName);
            List<JobExecution> jobExecutions = runningJobExecutions.stream().collect(Collectors.toList());
            for (JobExecution jobExecution : jobExecutions) {
                jobExecution.setStatus(BatchStatus.ABANDONED);
                jobExecution.setExitStatus(jobExecution.getExitStatus().and(new ExitStatus("ABANDONED")));
                jobRepository.update(jobExecution);
            }
        }
    }
}
