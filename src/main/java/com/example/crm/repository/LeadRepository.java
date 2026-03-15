package com.example.crm.repository;

import com.example.crm.entity.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    Page<Lead> findAllByIsActiveTrue(Pageable pageable);
    Optional<Lead> findByIdAndIsActiveTrue(Long id);
    Page<Lead> findAllByCreatedByIdAndIsActiveTrue(Long userId, Pageable pageable);
    boolean existsByEmailAndIdNot(String email, Long id);
}
