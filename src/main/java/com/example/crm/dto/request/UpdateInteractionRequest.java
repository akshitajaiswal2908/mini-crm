package com.example.crm.dto.request;

import lombok.Data;

@Data
public class UpdateInteractionRequest {

    private String interactionType;
    private String notes;
}