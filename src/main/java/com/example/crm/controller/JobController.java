package com.example.crm.controller;

import com.example.crm.dto.request.ApplyForJobRequest;
import com.example.crm.dto.request.CreateJobRequest;
import com.example.crm.dto.response.ApiResponse;
import com.example.crm.dto.response.ApplicationResponse;
import com.example.crm.dto.response.CandidateResponse;
import com.example.crm.dto.response.InteractionResponse;
import com.example.crm.dto.response.JobResponse;
import com.example.crm.dto.response.LeadResponse;
import com.example.crm.dto.response.PageResponse;
import com.example.crm.service.ApplicationService;
import com.example.crm.service.InteractionService;
import com.example.crm.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Management")
@SecurityRequirement(name = "bearerAuth")
public class JobController {

    private final JobService jobService;
    private final InteractionService interactionService;
    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @Valid @RequestBody CreateJobRequest request,
            @AuthenticationPrincipal String email) {
        JobResponse job = jobService.createJob(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<JobResponse>builder()
                        .success(true)
                        .message("Job created successfully")
                        .data(job)
                        .build()
        );
    }

    @PatchMapping("/{job_id}")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @PathVariable("job_id") Long jobId,
            @Valid @RequestBody CreateJobRequest request,
            @AuthenticationPrincipal String email) {
        JobResponse job = jobService.updateJob(jobId, request, email);
        return ResponseEntity.ok(
                ApiResponse.<JobResponse>builder()
                        .success(true)
                        .message("Job updated successfully")
                        .data(job)
                        .build()
        );
    }

    @DeleteMapping("/{job_id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable("job_id") Long jobId,
            @AuthenticationPrincipal String email) {
        jobService.deleteJob(jobId, email);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Job deleted successfully")
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> getMyJobs(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<JobResponse> jobs = jobService.getMyJobs(email, pageNo, pageSize);
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<JobResponse>>builder()
                        .success(true)
                        .message("Jobs retrieved successfully")
                        .data(jobs)
                        .build()
        );
    }

    @GetMapping("/{job_id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable("job_id") Long jobId) {
        JobResponse job = jobService.getJobById(jobId);
        return ResponseEntity.ok(
                ApiResponse.<JobResponse>builder()
                        .success(true)
                        .message("Job retrieved successfully")
                        .data(job)
                        .build()
        );
    }

    @Operation(tags = {"Interaction Management"})
    @GetMapping("/{job_id}/interactions")
    public ResponseEntity<ApiResponse<List<InteractionResponse>>> getJobInteractions(
            @PathVariable("job_id") Long jobId) {
        List<InteractionResponse> interactions = interactionService.getJobInteractions(jobId);
        return ResponseEntity.ok(
                ApiResponse.<List<InteractionResponse>>builder()
                        .success(true)
                        .message("Job interactions retrieved successfully")
                        .data(interactions)
                        .build()
        );
    }

    @Operation(tags = {"Interaction Management"})
    @GetMapping("/{job_id}/interactions/leads")
    public ResponseEntity<ApiResponse<List<LeadResponse>>> getJobInteractedLeads(
            @PathVariable("job_id") Long jobId) {
        List<LeadResponse> leads = interactionService.getJobInteractedLeads(jobId);
        return ResponseEntity.ok(
                ApiResponse.<List<LeadResponse>>builder()
                        .success(true)
                        .message("Interacted leads retrieved successfully")
                        .data(leads)
                        .build()
        );
    }

    @Operation(tags = {"Application Management"})
    @PostMapping("/{job_id}/applications")
    public ResponseEntity<ApiResponse<ApplicationResponse>> applyForJob(
            @PathVariable("job_id") Long jobId,
            @Valid @RequestBody ApplyForJobRequest request,
            @AuthenticationPrincipal String email) {
        ApplicationResponse application = applicationService.applyForJob(jobId, request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ApplicationResponse>builder()
                        .success(true)
                        .message("Application created successfully")
                        .data(application)
                        .build()
        );
    }

    @Operation(tags = {"Application Management"})
    @GetMapping("/{job_id}/applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getJobApplications(
            @PathVariable("job_id") Long jobId) {
        List<ApplicationResponse> applications = applicationService.getJobApplications(jobId);
        return ResponseEntity.ok(
                ApiResponse.<List<ApplicationResponse>>builder()
                        .success(true)
                        .message("Job applications retrieved successfully")
                        .data(applications)
                        .build()
        );
    }

    @Operation(tags = {"Candidate Management"})
    @GetMapping("/{job_id}/candidates")
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> getJobCandidates(
            @PathVariable("job_id") Long jobId) {
        List<CandidateResponse> candidates = applicationService.getJobCandidates(jobId);
        return ResponseEntity.ok(
                ApiResponse.<List<CandidateResponse>>builder()
                        .success(true)
                        .message("Job candidates retrieved successfully")
                        .data(candidates)
                        .build()
        );
    }
}