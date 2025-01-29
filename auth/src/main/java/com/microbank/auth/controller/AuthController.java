package com.microbank.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microbank.auth.dto.request.*;
import com.microbank.auth.dto.response.UserResponse;
import com.microbank.auth.response.BaseApiResponse;
import com.microbank.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseApiResponse<String>> registerUser(@RequestBody @Valid RegisterRequest request) {
        BaseApiResponse<String> response = authService.registerUser(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/activate")
    public ResponseEntity<BaseApiResponse<String>> activateUser(@RequestBody @Valid ActivationRequest request) {
        BaseApiResponse<String> response = authService.activateUser(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseApiResponse<Map<String, Object>>> loginUser(@RequestBody @Valid LoginRequest request) {
        BaseApiResponse<Map<String, Object>> response = authService.loginUser(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping(value = "/refresh-token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseApiResponse<Map<String, Object>>> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        BaseApiResponse<Map<String, Object>> response = authService.refreshToken(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<BaseApiResponse<String>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        BaseApiResponse<String> response = authService.forgotPassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<BaseApiResponse<UserResponse>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) throws JsonProcessingException {
        BaseApiResponse<UserResponse> response = authService.resetPassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/users/me")
    public ResponseEntity<BaseApiResponse<UserResponse>> getCurrentUsersProfile(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getClaimAsString("sub");
        BaseApiResponse<UserResponse> response = authService.getCurrentUser(keycloakId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<UserResponse>> getUserById(@Valid @PathVariable UUID userId) {
        BaseApiResponse<UserResponse> response = authService.getUserById(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<List<UserResponse>>> getAllUsers() {
        BaseApiResponse<List<UserResponse>> response = authService.getAllUsers();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<String>> deleteUserById(@PathVariable UUID userId) {
        BaseApiResponse<String> response = authService.deleteUser(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping("/admin/users/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<UserResponse>> updateUserRole(@RequestBody @Valid UpdateRoleRequest request) {
        BaseApiResponse<UserResponse> response = authService.updateUserRole(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping("/admin/users/access")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<UserResponse>> updateUserAccess(@RequestBody @Valid UpdateAccessRequest request) {
        BaseApiResponse<UserResponse> response = authService.updateUserAccess(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
