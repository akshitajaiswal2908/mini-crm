package com.example.crm.service.impl;

import com.example.crm.dto.request.ApplyForJobRequest;
import com.example.crm.dto.request.UpdateApplicationStatusRequest;
import com.example.crm.dto.response.ApplicationResponse;
import com.example.crm.dto.response.CandidateResponse;
import com.example.crm.entity.Application;
import com.example.crm.entity.Candidate;
import com.example.crm.entity.Job;
import com.example.crm.entity.User;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.exception.UnauthorizedAccessException;
import com.example.crm.mapper.ApplicationMapper;
import com.example.crm.mapper.CandidateMapper;
import com.example.crm.repository.ApplicationRepository;
import com.example.crm.repository.CandidateRepository;
import com.example.crm.repository.JobRepository;
import com.example.crm.repository.UserRepository;
import com.example.crm.service.ApplicationService;
import com.example.crm.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final ApplicationMapper applicationMapper;
    private final CandidateMapper candidateMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public ApplicationResponse applyForJob(Long jobId, ApplyForJobRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Job job = jobRepository.findByIdAndIsActiveTrue(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You can only apply candidates to your own jobs");
        }

        Candidate candidate = candidateRepository.findByIdAndIsActiveTrue(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        if (applicationRepository.existsByJobIdAndCandidateId(jobId, candidate.getId())) {
            throw new IllegalStateException("Candidate has already applied for this job");
        }

        Application application = Application.builder()
                .job(job)
                .candidate(candidate)
                .createdBy(user)
                .build();

        Application saved = applicationRepository.save(application);
        auditLogService.logCreate("Application", saved.getId());
        log.info("Application created: id={} by user={}", saved.getId(), email);
        return applicationMapper.toResponse(saved);
    }

    @Override
    public List<ApplicationResponse> getJobApplications(Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new ResourceNotFoundException("Job not found");
        }
        return applicationRepository.findAllByJobIdAndIsActiveTrue(jobId)
                .stream().map(applicationMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<CandidateResponse> getJobCandidates(Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new ResourceNotFoundException("Job not found");
        }
        return applicationRepository.findCandidatesByJobId(jobId)
                .stream().map(candidateMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponse> getCandidateApplications(Long candidateId) {
        if (!candidateRepository.existsById(candidateId)) {
            throw new ResourceNotFoundException("Candidate not found");
        }
        return applicationRepository.findAllByCandidateIdAndIsActiveTrue(candidateId)
                .stream().map(applicationMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public ApplicationResponse getApplicationById(Long applicationId) {
        Application application = applicationRepository.findByIdAndIsActiveTrue(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        return applicationMapper.toResponse(application);
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, UpdateApplicationStatusRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Application application = applicationRepository.findByIdAndIsActiveTrue(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getJob().getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You can only update status of applications for your own jobs");
        }

        String oldStatus = application.getStatus() != null ? application.getStatus().name() : null;
        auditLogService.logUpdate("Application", applicationId, "status", oldStatus, request.getStatus().name());
        application.setStatus(request.getStatus());
        return applicationMapper.toResponse(applicationRepository.save(application));
    }
}