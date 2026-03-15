package com.example.crm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInteractionRequest {

    @NotNull(message = "Job ID is required")
    private Long jobId;

    @NotBlank(message = "Interaction type is required")
    private String interactionType;

    private String notes;
}