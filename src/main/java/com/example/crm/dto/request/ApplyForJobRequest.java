package com.example.crm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplyForJobRequest {

    @NotNull(message = "Candidate ID is required")
    private Long candidateId;
}