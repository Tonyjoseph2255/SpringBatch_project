package com.ust.invoice.extract.entity;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobStatusResponse {
    private Long jobId;
    private String status;
    private String startTime;
    private String endTime;

    private String errorMessage;
}
