package com.example.crm.service.impl;

import com.example.crm.dto.request.CreateLeadRequest;
import com.example.crm.dto.response.LeadResponse;
import com.example.crm.dto.response.PageResponse;
import com.example.crm.entity.Lead;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.mapper.LeadMapper;
import com.example.crm.repository.LeadRepository;
import com.example.crm.repository.UserRepository;
import com.example.crm.service.AuditLogService;
import com.example.crm.service.LeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public LeadResponse createLead(CreateLeadRequest request, String email) {
        Lead lead = leadMapper.toEntity(request, email);
        Lead savedLead = leadRepository.save(lead);
        log.info("Lead created: id={} by user={}", savedLead.getId(), email);
        auditLogService.logCreate("Lead", savedLead.getId());
        return leadMapper.toResponse(savedLead);
    }

    @Override
    @Transactional
    public LeadResponse updateLead(Long id, CreateLeadRequest request) {
        Lead lead = leadRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));

        if (request.getFirstName() != null) {
            auditLogService.logUpdate("Lead", id, "firstName", lead.getFirstName(), request.getFirstName());
            lead.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            auditLogService.logUpdate("Lead", id, "lastName", lead.getLastName(), request.getLastName());
            lead.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            if (leadRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new IllegalStateException("Email already in use by another lead");
            }
            auditLogService.logUpdate("Lead", id, "email", lead.getEmail(), request.getEmail());
            lead.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            auditLogService.logUpdate("Lead", id, "phone", lead.getPhone(), request.getPhone());
            lead.setPhone(request.getPhone());
        }
        if (request.getStatus() != null) {
            auditLogService.logUpdate("Lead", id, "status",
                    lead.getStatus() != null ? lead.getStatus().name() : null,
                    request.getStatus().name());
            lead.setStatus(request.getStatus());
        }
        if (request.getSource() != null) {
            auditLogService.logUpdate("Lead", id, "source", lead.getSource(), request.getSource());
            lead.setSource(request.getSource());
        }

        Lead updatedLead = leadRepository.save(lead);
        return leadMapper.toResponse(updatedLead);
    }

    @Override
    public LeadResponse getLeadById(Long id) {
        Lead lead = leadRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));
        return leadMapper.toResponse(lead);
    }

    @Override
    public PageResponse<LeadResponse> getAllLeads(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Lead> page = leadRepository.findAllByIsActiveTrue(pageable);

        return PageResponse.<LeadResponse>builder()
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalRecords(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .data(page.getContent().stream()
                        .map(leadMapper::toResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public PageResponse<LeadResponse> getUserLeads(Long userId, int pageNo, int pageSize) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Lead> page = leadRepository.findAllByCreatedByIdAndIsActiveTrue(userId, pageable);

        return PageResponse.<LeadResponse>builder()
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalRecords(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .data(page.getContent().stream()
                        .map(leadMapper::toResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public void deleteLead(Long id) {
        Lead lead = leadRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));
        lead.setActive(false);
        leadRepository.save(lead);
        auditLogService.logDelete("Lead", id);
    }
}
