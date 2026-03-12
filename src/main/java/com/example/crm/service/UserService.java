package com.example.crm.service;

import com.example.crm.dto.request.CreateUserRequest;
import com.example.crm.dto.request.LoginRequest;
import com.example.crm.dto.response.LoginResponse;
import com.example.crm.dto.response.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    LoginResponse login(LoginRequest request);

    UserResponse getProfile(String email);


}