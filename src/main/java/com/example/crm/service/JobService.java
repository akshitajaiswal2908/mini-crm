package com.example.crm.service;

import com.example.crm.dto.request.CreateJobRequest;
import com.example.crm.dto.response.JobResponse;
import com.example.crm.dto.response.PageResponse;

public interface JobService {
    JobResponse createJob(CreateJobRequest request, String email);
    JobResponse updateJob(Long jobId, CreateJobRequest request, String email);
    void deleteJob(Long jobId, String email);
    PageResponse<JobResponse> getMyJobs(String email, int pageNo, int pageSize);
    JobResponse getJobById(Long jobId);
}