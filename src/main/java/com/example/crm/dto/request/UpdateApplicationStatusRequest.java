package com.example.crm.dto.request;

import com.example.crm.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {

    @NotNull(message = "Status is required")
    private ApplicationStatus status;
}