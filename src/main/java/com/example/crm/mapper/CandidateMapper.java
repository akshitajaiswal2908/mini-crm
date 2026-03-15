package com.example.crm.mapper;

import com.example.crm.dto.response.CandidateResponse;
import com.example.crm.entity.Candidate;
import org.springframework.stereotype.Component;

@Component
public class CandidateMapper {

    public CandidateResponse toResponse(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .firstName(candidate.getFirstName())
                .lastName(candidate.getLastName())
                .email(candidate.getEmail())
                .phone(candidate.getPhone())
                .leadId(candidate.getLead() != null ? candidate.getLead().getId() : null)
                .createdBy(candidate.getCreatedBy() != null ? candidate.getCreatedBy().getId() : null)
                .createdAt(candidate.getCreatedAt())
                .updatedAt(candidate.getUpdatedAt())
                .build();
    }
}