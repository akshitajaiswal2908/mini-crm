package com.example.crm.mapper;

import com.example.crm.dto.request.CreateJobRequest;
import com.example.crm.dto.response.JobResponse;
import com.example.crm.entity.Job;
import com.example.crm.entity.User;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    public Job toEntity(CreateJobRequest request, User createdBy) {
        return Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .status(request.getStatus())
                .createdBy(createdBy)
                .build();
    }

    public JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .status(job.getStatus())
                .createdBy(job.getCreatedBy() != null ? job.getCreatedBy().getId() : null)
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}