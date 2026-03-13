package com.example.crm.mapper;

import com.example.crm.dto.response.ApplicationResponse;
import com.example.crm.entity.Application;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {

    public ApplicationResponse toResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob() != null ? application.getJob().getId() : null)
                .candidateId(application.getCandidate() != null ? application.getCandidate().getId() : null)
                .status(application.getStatus())
                .createdBy(application.getCreatedBy() != null ? application.getCreatedBy().getId() : null)
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}