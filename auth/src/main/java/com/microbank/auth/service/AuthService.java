package com.microbank.auth.service;

import com.microbank.auth.dto.request.*;
import com.microbank.auth.dto.response.UserResponse;
import com.microbank.auth.response.BaseApiResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AuthService {

    BaseApiResponse<String> registerUser(RegisterRequest request);
    BaseApiResponse<String> activateUser(ActivationRequest request);
    BaseApiResponse<Map<String, Object>> loginUser(LoginRequest loginRequest);
    BaseApiResponse<Map<String, Object>> refreshToken(RefreshTokenRequest refreshTokenRequest);
    BaseApiResponse<String> forgotPassword(@Valid ForgotPasswordRequest request);
    BaseApiResponse<UserResponse> resetPassword(@Valid ResetPasswordRequest request);

    BaseApiResponse<UserResponse> getCurrentUser(String keycloakId);

    BaseApiResponse<UserResponse> getUserById(UUID userId);
    BaseApiResponse<List<UserResponse>> getAllUsers();
    BaseApiResponse<UserResponse> updateUserRole(UpdateRoleRequest request);
    BaseApiResponse<UserResponse> updateUserAccess(UpdateAccessRequest request);
    BaseApiResponse<String> deleteUser(UUID userId);

}
