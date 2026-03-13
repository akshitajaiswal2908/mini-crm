package com.example.crm.repository;

import com.example.crm.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findAllByCreatedByIdAndIsActiveTrue(Long userId, Pageable pageable);
    Optional<Job> findByIdAndIsActiveTrue(Long id);
    boolean existsByIdAndCreatedByIdAndIsActiveTrue(Long id, Long userId);
}