package com.example.crm.controller;

import com.example.crm.dto.request.UpdateApplicationStatusRequest;
import com.example.crm.dto.response.ApiResponse;
import com.example.crm.dto.response.ApplicationResponse;
import com.example.crm.service.ApplicationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Tag(name = "Application Management")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/{application_id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationById(
            @PathVariable("application_id") Long applicationId) {
        ApplicationResponse application = applicationService.getApplicationById(applicationId);
        return ResponseEntity.ok(
                ApiResponse.<ApplicationResponse>builder()
                        .success(true)
                        .message("Application retrieved successfully")
                        .data(application)
                        .build()
        );
    }

    @PatchMapping("/{application_id}/status")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplicationStatus(
            @PathVariable("application_id") Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request,
            @AuthenticationPrincipal String email) {
        ApplicationResponse application = applicationService.updateApplicationStatus(applicationId, request, email);
        return ResponseEntity.ok(
                ApiResponse.<ApplicationResponse>builder()
                        .success(true)
                        .message("Application status updated successfully")
                        .data(application)
                        .build()
        );
    }
}