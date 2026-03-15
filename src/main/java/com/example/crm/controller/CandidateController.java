package com.example.crm.controller;

import com.example.crm.dto.request.CreateCandidateRequest;
import com.example.crm.dto.request.UpdateCandidateRequest;
import com.example.crm.dto.response.ApiResponse;
import com.example.crm.dto.response.ApplicationResponse;
import com.example.crm.dto.response.CandidateResponse;
import com.example.crm.dto.response.PageResponse;
import com.example.crm.service.ApplicationService;
import com.example.crm.service.CandidateService;
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
@RequestMapping("/candidates")
@RequiredArgsConstructor
@Tag(name = "Candidate Management")
@SecurityRequirement(name = "bearerAuth")
public class CandidateController {

    private final CandidateService candidateService;
    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApiResponse<CandidateResponse>> createCandidate(
            @Valid @RequestBody CreateCandidateRequest request,
            @AuthenticationPrincipal String email) {
        CandidateResponse candidate = candidateService.createCandidate(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CandidateResponse>builder()
                        .success(true)
                        .message("Candidate created successfully")
                        .data(candidate)
                        .build()
        );
    }

    @GetMapping("/{candidate_id}")
    public ResponseEntity<ApiResponse<CandidateResponse>> getCandidateById(
            @PathVariable("candidate_id") Long candidateId) {
        CandidateResponse candidate = candidateService.getCandidateById(candidateId);
        return ResponseEntity.ok(
                ApiResponse.<CandidateResponse>builder()
                        .success(true)
                        .message("Candidate retrieved successfully")
                        .data(candidate)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CandidateResponse>>> getAllCandidates(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<CandidateResponse> candidates = candidateService.getAllCandidates(pageNo, pageSize);
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<CandidateResponse>>builder()
                        .success(true)
                        .message("Candidates retrieved successfully")
                        .data(candidates)
                        .build()
        );
    }

    @GetMapping("/{candidate_id}/applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getCandidateApplications(
            @PathVariable("candidate_id") Long candidateId) {
        List<ApplicationResponse> applications = applicationService.getCandidateApplications(candidateId);
        return ResponseEntity.ok(
                ApiResponse.<List<ApplicationResponse>>builder()
                        .success(true)
                        .message("Candidate applications retrieved successfully")
                        .data(applications)
                        .build()
        );
    }

    @PatchMapping("/{candidate_id}")
    public ResponseEntity<ApiResponse<CandidateResponse>> updateCandidate(
            @PathVariable("candidate_id") Long candidateId,
            @Valid @RequestBody UpdateCandidateRequest request,
            @AuthenticationPrincipal String email) {
        CandidateResponse candidate = candidateService.updateCandidate(candidateId, request, email);
        return ResponseEntity.ok(
                ApiResponse.<CandidateResponse>builder()
                        .success(true)
                        .message("Candidate updated successfully")
                        .data(candidate)
                        .build()
        );
    }

    @DeleteMapping("/{candidate_id}")
    public ResponseEntity<ApiResponse<Void>> deleteCandidate(
            @PathVariable("candidate_id") Long candidateId,
            @AuthenticationPrincipal String email) {
        candidateService.deleteCandidate(candidateId, email);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Candidate deleted successfully")
                        .build()
        );
    }
}