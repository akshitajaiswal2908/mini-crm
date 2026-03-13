package com.example.crm.mapper;

import com.example.crm.dto.request.CreateLeadRequest;
import com.example.crm.dto.response.LeadResponse;
import com.example.crm.entity.Lead;
import com.example.crm.entity.User;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeadMapper {

    private final UserRepository userRepository;

    public Lead toEntity(CreateLeadRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return Lead.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(request.getStatus())
                .source(request.getSource())
                .createdBy(user)
                .build();
    }

    public LeadResponse toResponse(Lead lead) {
        return LeadResponse.builder()
                .id(lead.getId())
                .firstName(lead.getFirstName())
                .lastName(lead.getLastName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .status(lead.getStatus())
                .source(lead.getSource())
                .createdBy(lead.getCreatedBy().getId())
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .build();
    }
}
