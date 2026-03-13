package com.example.crm.controller;

import com.example.crm.dto.request.CreateInteractionRequest;
import com.example.crm.dto.request.UpdateInteractionRequest;
import com.example.crm.dto.response.ApiResponse;
import com.example.crm.dto.response.InteractionResponse;
import com.example.crm.service.InteractionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Interaction Management")
@SecurityRequirement(name = "bearerAuth")
public class InteractionController {

    private final InteractionService interactionService;

    @PostMapping("/leads/{lead_id}/interactions")
    public ResponseEntity<ApiResponse<InteractionResponse>> createInteraction(
            @PathVariable("lead_id") Long leadId,
            @Valid @RequestBody CreateInteractionRequest request,
            @AuthenticationPrincipal String email) {
        InteractionResponse interaction = interactionService.createInteraction(leadId, request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<InteractionResponse>builder()
                        .success(true)
                        .message("Interaction created successfully")
                        .data(interaction)
                        .build()
        );
    }

    @GetMapping("/leads/{lead_id}/interactions")
    public ResponseEntity<ApiResponse<List<InteractionResponse>>> getLeadInteractions(
            @PathVariable("lead_id") Long leadId) {
        List<InteractionResponse> interactions = interactionService.getLeadInteractions(leadId);
        return ResponseEntity.ok(
                ApiResponse.<List<InteractionResponse>>builder()
                        .success(true)
                        .message("Lead interactions retrieved successfully")
                        .data(interactions)
                        .build()
        );
    }

    @PatchMapping("/interactions/{interaction_id}")
    public ResponseEntity<ApiResponse<InteractionResponse>> updateInteraction(
            @PathVariable("interaction_id") Long interactionId,
            @Valid @RequestBody UpdateInteractionRequest request,
            @AuthenticationPrincipal String email) {
        InteractionResponse interaction = interactionService.updateInteraction(interactionId, request, email);
        return ResponseEntity.ok(
                ApiResponse.<InteractionResponse>builder()
                        .success(true)
                        .message("Interaction updated successfully")
                        .data(interaction)
                        .build()
        );
    }

    @DeleteMapping("/interactions/{interaction_id}")
    public ResponseEntity<ApiResponse<Void>> deleteInteraction(
            @PathVariable("interaction_id") Long interactionId,
            @AuthenticationPrincipal String email) {
        interactionService.deleteInteraction(interactionId, email);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Interaction deleted successfully")
                        .build()
        );
    }

    @GetMapping("/interactions")
    public ResponseEntity<ApiResponse<List<InteractionResponse>>> getMyInteractions(
            @AuthenticationPrincipal String email) {
        List<InteractionResponse> interactions = interactionService.getMyInteractions(email);
        return ResponseEntity.ok(
                ApiResponse.<List<InteractionResponse>>builder()
                        .success(true)
                        .message("Interactions retrieved successfully")
                        .data(interactions)
                        .build()
        );
    }
}