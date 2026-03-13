package com.example.crm.dto.response;

import com.example.crm.entity.JobStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonPropertyOrder({"id", "title", "description", "location", "status", "createdBy", "createdAt", "updatedAt"})
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private JobStatus status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}