package com.example.crm.repository;

import com.example.crm.entity.Application;
import com.example.crm.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByJobIdAndCandidateId(Long jobId, Long candidateId);
    Optional<Application> findByIdAndIsActiveTrue(Long id);
    List<Application> findAllByJobIdAndIsActiveTrue(Long jobId);
    List<Application> findAllByCandidateIdAndIsActiveTrue(Long candidateId);

    @Query("SELECT a.candidate FROM Application a WHERE a.job.id = :jobId AND a.isActive = true")
    List<Candidate> findCandidatesByJobId(@Param("jobId") Long jobId);
}