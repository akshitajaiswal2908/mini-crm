package com.example.crm.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonPropertyOrder({"id", "jobId", "leadId", "interactionType", "notes", "createdBy", "createdAt", "updatedAt"})
public class InteractionResponse {
    private Long id;
    private Long jobId;
    private Long leadId;
    private String interactionType;
    private String notes;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}