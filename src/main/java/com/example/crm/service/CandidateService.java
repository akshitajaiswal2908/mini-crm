package com.example.crm.service;

import com.example.crm.dto.request.CreateCandidateRequest;
import com.example.crm.dto.request.UpdateCandidateRequest;
import com.example.crm.dto.response.CandidateResponse;
import com.example.crm.dto.response.PageResponse;

public interface CandidateService {
    CandidateResponse createCandidate(CreateCandidateRequest request, String email);
    CandidateResponse updateCandidate(Long candidateId, UpdateCandidateRequest request, String email);
    CandidateResponse getCandidateById(Long candidateId);
    PageResponse<CandidateResponse> getAllCandidates(int pageNo, int pageSize);
    PageResponse<CandidateResponse> getUserCandidates(Long userId, int pageNo, int pageSize);
    void deleteCandidate(Long candidateId, String email);
}