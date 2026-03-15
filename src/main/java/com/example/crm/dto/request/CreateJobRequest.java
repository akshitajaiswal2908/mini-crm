package com.example.crm.dto.request;

import com.example.crm.entity.JobStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateJobRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Status is required")
    private JobStatus status;
}