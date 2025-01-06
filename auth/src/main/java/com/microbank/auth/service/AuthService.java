package com.microbank.auth.service;

import com.microbank.auth.dto.request.ActivationRequest;
import com.microbank.auth.dto.request.LoginRequest;
import com.microbank.auth.dto.request.RefreshTokenRequest;
import com.microbank.auth.dto.request.RegisterRequest;
import com.microbank.auth.dto.response.UserResponse;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AuthService {

    List<UserResponse> getAllUsers();
    void deleteUser(UUID id);

    String registerUser(RegisterRequest request);
    void saveUserToRedis(RegisterRequest request, String activationCode);
    String generateActivationCode();
    String activateUser(ActivationRequest request);
    Map<String, Object> loginUser(LoginRequest loginRequest);
    UserResponse getUserById(UUID userId);

    UserResponse getUserByKeycloakId(String keycloakId);

    Map<String, Object> refreshToken(RefreshTokenRequest refreshTokenRequest);


}
