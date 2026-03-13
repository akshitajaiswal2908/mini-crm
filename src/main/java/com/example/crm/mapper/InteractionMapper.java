package com.example.crm.mapper;

import com.example.crm.dto.response.InteractionResponse;
import com.example.crm.entity.Interaction;
import org.springframework.stereotype.Component;

@Component
public class InteractionMapper {

    public InteractionResponse toResponse(Interaction interaction) {
        return InteractionResponse.builder()
                .id(interaction.getId())
                .jobId(interaction.getJob() != null ? interaction.getJob().getId() : null)
                .leadId(interaction.getLead() != null ? interaction.getLead().getId() : null)
                .interactionType(interaction.getInteractionType())
                .notes(interaction.getNotes())
                .createdBy(interaction.getCreatedBy() != null ? interaction.getCreatedBy().getId() : null)
                .createdAt(interaction.getCreatedAt())
                .updatedAt(interaction.getUpdatedAt())
                .build();
    }
}