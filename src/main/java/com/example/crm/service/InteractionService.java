package com.example.crm.service;

import com.example.crm.dto.request.CreateInteractionRequest;
import com.example.crm.dto.request.UpdateInteractionRequest;
import com.example.crm.dto.response.InteractionResponse;
import com.example.crm.dto.response.LeadResponse;

import java.util.List;

public interface InteractionService {
    InteractionResponse createInteraction(Long leadId, CreateInteractionRequest request, String email);
    List<InteractionResponse> getLeadInteractions(Long leadId);
    InteractionResponse updateInteraction(Long interactionId, UpdateInteractionRequest request, String email);
    void deleteInteraction(Long interactionId, String email);
    List<InteractionResponse> getJobInteractions(Long jobId);
    List<LeadResponse> getJobInteractedLeads(Long jobId);
    List<InteractionResponse> getMyInteractions(String email);
}