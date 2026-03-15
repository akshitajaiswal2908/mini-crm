package com.example.crm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCandidateRequest {

    @NotNull(message = "Lead ID is required")
    private Long leadId;

    @NotNull(message = "Job ID is required")
    private Long jobId;
}