package com.example.crm.dto.response;

import com.example.crm.entity.LeadStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonPropertyOrder({"id", "firstName", "lastName", "email", "phone", "status", "source", "createdBy", "createdAt", "updatedAt"})
public class LeadResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LeadStatus status;
    private String source;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
