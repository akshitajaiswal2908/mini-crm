package com.example.crm.controller;

import com.example.crm.dto.request.CreateUserRequest;
import com.example.crm.dto.request.LoginRequest;
import com.example.crm.dto.response.ApiResponse;
import com.example.crm.dto.response.CandidateResponse;
import com.example.crm.dto.response.LeadResponse;
import com.example.crm.dto.response.LoginResponse;
import com.example.crm.dto.response.PageResponse;
import com.example.crm.dto.response.UserResponse;
import com.example.crm.service.CandidateService;
import com.example.crm.service.LeadService;
import com.example.crm.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management")
public class UserController {

    private final UserService userService;
    private final LeadService leadService;
    private final CandidateService candidateService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("User registered successfully")
                        .data(user)
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(
                ApiResponse.<LoginResponse>builder()
                        .success(true)
                        .message("Login successful")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{user_id}/leads")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PageResponse<LeadResponse>>> getUserLeads(
            @PathVariable("user_id") Long userId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<LeadResponse> leads = leadService.getUserLeads(userId, pageNo, pageSize);
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<LeadResponse>>builder()
                        .success(true)
                        .message("User leads retrieved successfully")
                        .data(leads)
                        .build()
        );
    }

    @GetMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@AuthenticationPrincipal String email) {
        UserResponse response = userService.getProfile(email);
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("Profile retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{user_id}/candidates")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PageResponse<CandidateResponse>>> getUserCandidates(
            @PathVariable("user_id") Long userId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<CandidateResponse> candidates = candidateService.getUserCandidates(userId, pageNo, pageSize);
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<CandidateResponse>>builder()
                        .success(true)
                        .message("User candidates retrieved successfully")
                        .data(candidates)
                        .build()
        );
    }

}