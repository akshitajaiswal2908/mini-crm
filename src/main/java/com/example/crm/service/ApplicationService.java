package com.example.crm.service;

import com.example.crm.dto.request.ApplyForJobRequest;
import com.example.crm.dto.request.UpdateApplicationStatusRequest;
import com.example.crm.dto.response.ApplicationResponse;
import com.example.crm.dto.response.CandidateResponse;

import java.util.List;

public interface ApplicationService {
    ApplicationResponse applyForJob(Long jobId, ApplyForJobRequest request, String email);
    List<ApplicationResponse> getJobApplications(Long jobId);
    List<CandidateResponse> getJobCandidates(Long jobId);
    List<ApplicationResponse> getCandidateApplications(Long candidateId);
    ApplicationResponse getApplicationById(Long applicationId);
    ApplicationResponse updateApplicationStatus(Long applicationId, UpdateApplicationStatusRequest request, String email);
}