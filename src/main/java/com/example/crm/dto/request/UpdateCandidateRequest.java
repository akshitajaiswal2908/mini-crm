package com.example.crm.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateCandidateRequest {
    private String firstName;
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;
}