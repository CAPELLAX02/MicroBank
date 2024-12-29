package com.microbank.auth.service;

import com.microbank.auth.dto.request.ActivationRequest;
import com.microbank.auth.dto.request.LoginRequest;
import com.microbank.auth.dto.request.RegisterRequest;
import com.microbank.auth.dto.response.UserResponse;

import java.util.Map;

public interface AuthService {

    String registerUser(RegisterRequest request);
    void saveUserToRedis(RegisterRequest request, String activationCode);
    String generateActivationCode();
    String activateUser(ActivationRequest request);
    Map<String, Object> loginUser(LoginRequest loginRequest);
    UserResponse getUserById(Long userId);

}
