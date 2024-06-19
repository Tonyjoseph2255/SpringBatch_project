package com.ust.invoice.extract.service;

import com.ust.invoice.extract.entity.InvoiceExecutionStatus;
import com.ust.invoice.extract.entity.JobStatusResponse;
import com.ust.invoice.extract.util.DateTimeUtil;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class InvoiceMonitoringService {

    private final JobExplorer jobExplorer;

    @Autowired
    public InvoiceMonitoringService(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

    public List<InvoiceExecutionStatus> getAllJobExecutionStatuses() {
        Collection<String> jobNames = jobExplorer.getJobNames();  // Collection or List
        Set<String> jobNamesSet = new HashSet<>(jobNames);
        return jobNamesSet.stream()
                .flatMap(jobName -> jobExplorer.findJobInstancesByJobName(jobName, 0, Integer.MAX_VALUE).stream())
                .flatMap(jobInstance -> jobExplorer.getJobExecutions(jobInstance).stream())
                .map(this::mapToJobExecutionStatus)
                .collect(Collectors.toList());
    }

    private InvoiceExecutionStatus mapToJobExecutionStatus(JobExecution jobExecution) {
        return new InvoiceExecutionStatus(
                jobExecution.getId(),
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus().toString(),
                jobExecution.getExitStatus().getExitDescription()
        );
    }

    public List<InvoiceExecutionStatus> getFailedJobExecutionStatuses() {
        Collection<String> jobNames = jobExplorer.getJobNames();  // Collection or List
        Set<String> jobNamesSet = new HashSet<>(jobNames);
        return jobNamesSet.stream()
                .flatMap(jobName -> jobExplorer.findJobInstancesByJobName(jobName, 0, Integer.MAX_VALUE).stream())
                .flatMap(jobInstance -> jobExplorer.getJobExecutions(jobInstance).stream())
                .filter(jobExecution -> jobExecution.getStatus() == BatchStatus.FAILED)
                .map(this::mapToJobExecutionStatus)
                .collect(Collectors.toList());
    }
    public List<JobStatusResponse> getLast10JobStatuses(String jobName) {
        List<JobInstance> jobInstances = jobExplorer.findJobInstancesByJobName(jobName, 0, 10);
        return jobInstances.stream()
                .flatMap(jobInstance -> jobExplorer.getJobExecutions(jobInstance).stream())
                .sorted((je1, je2) -> je2.getStartTime().compareTo(je1.getStartTime()))
                .limit(10)
                .map(this::convertToJobStatusResponse)
                .collect(Collectors.toList());
    }
    private JobStatusResponse convertToJobStatusResponse(JobExecution jobExecution) {
        String status = jobExecution.getStatus().toString();
        String errorMessage = jobExecution.getExitStatus().getExitDescription();
        switch (status) {
            case "FAILED":
                errorMessage = extractErrorMessage(errorMessage);
                break;
            case "COMPLETED":
                errorMessage = "No errors";
                break;
            case "STARTING":
            case "STARTED":
                status = "RUNNING";
                break;
            case "STOPPING":
            case "STOPPED":
                status = "STOPPED";
                break;
            case "ABANDONED":
                status = "ABANDONED";
                break;
            default:
                status = "EXECUTING";

        }
        return JobStatusResponse.builder()
                .jobId(jobExecution.getJobId())
                .status(status)
                .startTime(DateTimeUtil.getDateTime(jobExecution.getStartTime()))
                .endTime(DateTimeUtil.getDateTime(jobExecution.getEndTime()))
                .errorMessage(errorMessage)
                .build();
    }private String extractErrorMessage(String errorMessage) {
        Pattern pattern = Pattern.compile("^[^:]*: (.*)");
        Matcher matcher = pattern.matcher(errorMessage);
        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return errorMessage.lines().findFirst().orElse("No detailed error message available").trim();
        }
    }
}

