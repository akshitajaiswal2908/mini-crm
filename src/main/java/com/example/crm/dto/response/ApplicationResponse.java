package com.example.crm.dto.response;

import com.example.crm.entity.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonPropertyOrder({"id", "jobId", "candidateId", "status", "createdBy", "createdAt", "updatedAt"})
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private Long candidateId;
    private ApplicationStatus status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}