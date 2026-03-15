package com.example.crm.service;

import com.example.crm.dto.request.CreateLeadRequest;
import com.example.crm.dto.response.LeadResponse;
import com.example.crm.dto.response.PageResponse;

public interface LeadService {
    LeadResponse createLead(CreateLeadRequest request, String email);
    LeadResponse updateLead(Long id, CreateLeadRequest request);
    LeadResponse getLeadById(Long id);
    PageResponse<LeadResponse> getAllLeads(int pageNo, int pageSize);
    void deleteLead(Long id);
    PageResponse<LeadResponse> getUserLeads(Long userId, int pageNo, int pageSize);
}
