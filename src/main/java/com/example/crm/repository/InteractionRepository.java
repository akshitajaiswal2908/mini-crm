package com.example.crm.repository;

import com.example.crm.entity.Interaction;
import com.example.crm.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    Optional<Interaction> findByIdAndIsActiveTrue(Long id);
    List<Interaction> findAllByLeadIdAndIsActiveTrue(Long leadId);
    List<Interaction> findAllByCreatedByIdAndIsActiveTrue(Long userId);
    List<Interaction> findAllByJobIdAndIsActiveTrue(Long jobId);

    @Query("SELECT DISTINCT i.lead FROM Interaction i WHERE i.job.id = :jobId AND i.isActive = true")
    List<Lead> findDistinctLeadsByJobId(@Param("jobId") Long jobId);
}