package com.example.crm.service.impl;

import com.example.crm.dto.request.CreateInteractionRequest;
import com.example.crm.dto.request.UpdateInteractionRequest;
import com.example.crm.dto.response.InteractionResponse;
import com.example.crm.dto.response.LeadResponse;
import com.example.crm.entity.Interaction;
import com.example.crm.entity.Job;
import com.example.crm.entity.Lead;
import com.example.crm.entity.User;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.exception.UnauthorizedAccessException;
import com.example.crm.mapper.InteractionMapper;
import com.example.crm.mapper.LeadMapper;
import com.example.crm.repository.InteractionRepository;
import com.example.crm.repository.JobRepository;
import com.example.crm.repository.LeadRepository;
import com.example.crm.repository.UserRepository;
import com.example.crm.service.AuditLogService;
import com.example.crm.service.InteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InteractionServiceImpl implements InteractionService {

    private final InteractionRepository interactionRepository;
    private final JobRepository jobRepository;
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final InteractionMapper interactionMapper;
    private final LeadMapper leadMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public InteractionResponse createInteraction(Long leadId, CreateInteractionRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Lead lead = leadRepository.findByIdAndIsActiveTrue(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));

        Job job = jobRepository.findByIdAndIsActiveTrue(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!job.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You can only create interactions using your own jobs");
        }

        Interaction interaction = Interaction.builder()
                .job(job)
                .lead(lead)
                .interactionType(request.getInteractionType())
                .notes(request.getNotes())
                .createdBy(user)
                .build();

        Interaction saved = interactionRepository.save(interaction);
        auditLogService.logCreate("Interaction", saved.getId());
        log.info("Interaction created: id={} by user={}", saved.getId(), email);
        return interactionMapper.toResponse(saved);
    }

    @Override
    public List<InteractionResponse> getLeadInteractions(Long leadId) {
        if (!leadRepository.existsById(leadId)) {
            throw new ResourceNotFoundException("Lead not found");
        }
        return interactionRepository.findAllByLeadIdAndIsActiveTrue(leadId)
                .stream().map(interactionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InteractionResponse updateInteraction(Long interactionId, UpdateInteractionRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Interaction interaction = interactionRepository.findByIdAndIsActiveTrue(interactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Interaction not found"));

        if (!interaction.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You can only update your own interactions");
        }

        if (request.getInteractionType() != null) {
            auditLogService.logUpdate("Interaction", interactionId, "interactionType",
                    interaction.getInteractionType(), request.getInteractionType());
            interaction.setInteractionType(request.getInteractionType());
        }
        if (request.getNotes() != null) {
            auditLogService.logUpdate("Interaction", interactionId, "notes",
                    interaction.getNotes(), request.getNotes());
            interaction.setNotes(request.getNotes());
        }

        return interactionMapper.toResponse(interactionRepository.save(interaction));
    }

    @Override
    @Transactional
    public void deleteInteraction(Long interactionId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Interaction interaction = interactionRepository.findByIdAndIsActiveTrue(interactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Interaction not found"));

        if (!interaction.getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You can only delete your own interactions");
        }

        auditLogService.logDelete("Interaction", interactionId);
        interaction.setActive(false);
        interactionRepository.save(interaction);
        log.info("Interaction soft-deleted: id={} by user={}", interactionId, email);
    }

    @Override
    public List<InteractionResponse> getJobInteractions(Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new ResourceNotFoundException("Job not found");
        }
        return interactionRepository.findAllByJobIdAndIsActiveTrue(jobId)
                .stream().map(interactionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<LeadResponse> getJobInteractedLeads(Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new ResourceNotFoundException("Job not found");
        }
        return interactionRepository.findDistinctLeadsByJobId(jobId)
                .stream().map(leadMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<InteractionResponse> getMyInteractions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return interactionRepository.findAllByCreatedByIdAndIsActiveTrue(user.getId())
                .stream().map(interactionMapper::toResponse).collect(Collectors.toList());
    }
}