package com.example.crm.service.impl;

import com.example.crm.dto.request.CreateCandidateRequest;
import com.example.crm.dto.request.UpdateCandidateRequest;
import com.example.crm.dto.response.CandidateResponse;
import com.example.crm.dto.response.PageResponse;
import com.example.crm.entity.Candidate;
import com.example.crm.entity.Lead;
import com.example.crm.entity.LeadStatus;
import com.example.crm.entity.User;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.exception.UnauthorizedAccessException;
import com.example.crm.mapper.CandidateMapper;
import com.example.crm.repository.CandidateRepository;
import com.example.crm.repository.JobRepository;
import com.example.crm.repository.LeadRepository;
import com.example.crm.repository.UserRepository;
import com.example.crm.service.AuditLogService;
import com.example.crm.service.CandidateService;
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
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final LeadRepository leadRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CandidateMapper candidateMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public CandidateResponse createCandidate(CreateCandidateRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Lead lead = leadRepository.findByIdAndIsActiveTrue(request.getLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));

        if (!jobRepository.existsByIdAndCreatedByIdAndIsActiveTrue(request.getJobId(), user.getId())) {
            throw new UnauthorizedAccessException("You can only convert leads using your own jobs");
        }

        if (candidateRepository.existsByLeadId(lead.getId())) {
            throw new IllegalStateException("This lead has already been converted to a candidate");
        }

        lead.setStatus(LeadStatus.CONVERTED);
        leadRepository.save(lead);

        Candidate candidate = Candidate.builder()
                .firstName(lead.getFirstName())
                .lastName(lead.getLastName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .lead(lead)
                .createdBy(user)
                .build();

        Candidate saved = candidateRepository.save(candidate);
        auditLogService.logCreate("Candidate", saved.getId());
        log.info("Candidate created: id={} by user={}", saved.getId(), email);
        return candidateMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CandidateResponse updateCandidate(Long candidateId, UpdateCandidateRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Candidate candidate = candidateRepository.findByIdAndIsActiveTrue(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        if (!candidate.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You can only update your own candidates");
        }

        if (request.getFirstName() != null) {
            auditLogService.logUpdate("Candidate", candidateId, "firstName", candidate.getFirstName(), request.getFirstName());
            candidate.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            auditLogService.logUpdate("Candidate", candidateId, "lastName", candidate.getLastName(), request.getLastName());
            candidate.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            if (candidateRepository.existsByEmailAndIdNot(request.getEmail(), candidateId)) {
                throw new IllegalStateException("Email already in use by another candidate");
            }
            auditLogService.logUpdate("Candidate", candidateId, "email", candidate.getEmail(), request.getEmail());
            candidate.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            auditLogService.logUpdate("Candidate", candidateId, "phone", candidate.getPhone(), request.getPhone());
            candidate.setPhone(request.getPhone());
        }

        Candidate updated = candidateRepository.save(candidate);
        log.info("Candidate updated: id={} by user={}", candidateId, email);
        return candidateMapper.toResponse(updated);
    }

    @Override
    public CandidateResponse getCandidateById(Long candidateId) {
        Candidate candidate = candidateRepository.findByIdAndIsActiveTrue(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));
        return candidateMapper.toResponse(candidate);
    }

    @Override
    public PageResponse<CandidateResponse> getAllCandidates(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Candidate> page = candidateRepository.findAllByIsActiveTrue(pageable);

        return PageResponse.<CandidateResponse>builder()
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalRecords(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .data(page.getContent().stream().map(candidateMapper::toResponse).collect(Collectors.toList()))
                .build();
    }

    @Override
    public PageResponse<CandidateResponse> getUserCandidates(Long userId, int pageNo, int pageSize) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Candidate> page = candidateRepository.findAllByCreatedByIdAndIsActiveTrue(userId, pageable);

        return PageResponse.<CandidateResponse>builder()
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalRecords(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .data(page.getContent().stream().map(candidateMapper::toResponse).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public void deleteCandidate(Long candidateId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Candidate candidate = candidateRepository.findByIdAndIsActiveTrue(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        if (!candidate.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You can only delete your own candidates");
        }

        candidate.setActive(false);
        candidateRepository.save(candidate);
        auditLogService.logDelete("Candidate", candidateId);
        log.info("Candidate soft-deleted: id={} by user={}", candidateId, email);
    }
}