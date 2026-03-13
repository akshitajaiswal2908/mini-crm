package com.example.crm.controller;

import com.example.crm.dto.request.CreateLeadRequest;
import com.example.crm.dto.response.ApiResponse;
import com.example.crm.dto.response.LeadResponse;
import com.example.crm.dto.response.PageResponse;
import com.example.crm.service.LeadService;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/leads")
@RequiredArgsConstructor
@Tag(name = "Lead Management")
@SecurityRequirement(name = "bearerAuth")
public class LeadController {

    private final LeadService leadService;

    @ApiResponses(@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Lead created successfully"))
    @PostMapping
    public ResponseEntity<ApiResponse<LeadResponse>> createLead(
            @Valid @RequestBody CreateLeadRequest request,
            @AuthenticationPrincipal String email) {
        LeadResponse lead = leadService.createLead(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<LeadResponse>builder()
                        .success(true)
                        .message("Lead created successfully")
                        .data(lead)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<LeadResponse>>> getAllLeads(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<LeadResponse> leads = leadService.getAllLeads(pageNo, pageSize);
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<LeadResponse>>builder()
                        .success(true)
                        .message("Leads retrieved successfully")
                        .data(leads)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> getLeadById(@PathVariable Long id) {
        LeadResponse lead = leadService.getLeadById(id);
        return ResponseEntity.ok(
                ApiResponse.<LeadResponse>builder()
                        .success(true)
                        .message("Lead retrieved successfully")
                        .data(lead)
                        .build()
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLead(@PathVariable Long id,
                                                                @Valid @RequestBody CreateLeadRequest request) {
        LeadResponse lead = leadService.updateLead(id, request);
        return ResponseEntity.ok(
                ApiResponse.<LeadResponse>builder()
                        .success(true)
                        .message("Lead updated successfully")
                        .data(lead)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Lead deleted successfully")
                        .build()
        );
    }
}
