package com.example.crm.service.impl;

import com.example.crm.dto.request.CreateJobRequest;
import com.example.crm.dto.response.JobResponse;
import com.example.crm.dto.response.PageResponse;
import com.example.crm.entity.Job;
import com.example.crm.entity.User;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.exception.UnauthorizedAccessException;
import com.example.crm.mapper.JobMapper;
import com.example.crm.repository.JobRepository;
import com.example.crm.repository.UserRepository;
import com.example.crm.service.AuditLogService;
import com.example.crm.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobMapper jobMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public JobResponse createJob(CreateJobRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Job job = jobMapper.toEntity(request, user);
        Job savedJob = jobRepository.save(job);
        auditLogService.logCreate("Job", savedJob.getId());
        log.info("Job created: id={} by user={}", savedJob.getId(), email);
        return jobMapper.toResponse(savedJob);
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long jobId, CreateJobRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Job job = jobRepository.findByIdAndIsActiveTrue(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You can only update your own jobs");
        }

        if (request.getTitle() != null) {
            auditLogService.logUpdate("Job", jobId, "title", job.getTitle(), request.getTitle());
            job.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            auditLogService.logUpdate("Job", jobId, "description", job.getDescription(), request.getDescription());
            job.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            auditLogService.logUpdate("Job", jobId, "location", job.getLocation(), request.getLocation());
            job.setLocation(request.getLocation());
        }
        if (request.getStatus() != null) {
            auditLogService.logUpdate("Job", jobId, "status",
                    job.getStatus() != null ? job.getStatus().name() : null,
                    request.getStatus().name());
            job.setStatus(request.getStatus());
        }

        return jobMapper.toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public void deleteJob(Long jobId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Job job = jobRepository.findByIdAndIsActiveTrue(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You can only delete your own jobs");
        }

        job.setActive(false);
        jobRepository.save(job);
        auditLogService.logDelete("Job", jobId);
        log.info("Job soft-deleted: id={} by user={}", jobId, email);
    }

    @Override
    public PageResponse<JobResponse> getMyJobs(String email, int pageNo, int pageSize) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Job> page = jobRepository.findAllByCreatedByIdAndIsActiveTrue(user.getId(), pageable);

        return PageResponse.<JobResponse>builder()
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalRecords(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .data(page.getContent().stream().map(jobMapper::toResponse).collect(Collectors.toList()))
                .build();
    }

    @Override
    public JobResponse getJobById(Long jobId) {
        Job job = jobRepository.findByIdAndIsActiveTrue(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return jobMapper.toResponse(job);
    }
}