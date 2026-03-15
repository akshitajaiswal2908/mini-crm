package com.example.crm.repository;

import com.example.crm.entity.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    boolean existsByLeadId(Long leadId);
    boolean existsByEmailAndIdNot(String email, Long id);
    Optional<Candidate> findByIdAndIsActiveTrue(Long id);
    Optional<Candidate> findByLeadIdAndIsActiveTrue(Long leadId);
    Page<Candidate> findAllByIsActiveTrue(Pageable pageable);
    Page<Candidate> findAllByCreatedByIdAndIsActiveTrue(Long userId, Pageable pageable);
}