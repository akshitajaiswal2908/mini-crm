package com.example.crm.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Long phone;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}
