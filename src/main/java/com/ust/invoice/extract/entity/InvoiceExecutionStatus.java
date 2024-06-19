package com.ust.invoice.extract.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class InvoiceExecutionStatus {
    private Long jobId;
    private String jobName;
    private String status;
    private String exitDescription;
}
