package com.example.crm.dto.request;

import com.example.crm.entity.LeadStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateLeadRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull(message = "Status is required")
    private LeadStatus status;

    @NotBlank(message = "Source is required")
    private String source;

}