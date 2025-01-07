package com.microbank.auth.controller;

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
    public ResponseEntity<BaseApiResponse<String>> registerUser(
            @RequestBody @Valid RegisterRequest request
    ) {
        return ResponseEntity.status(201).body(authService.registerUser(request));
    }

    @PostMapping("/activate")
    public ResponseEntity<BaseApiResponse<String>> activateUser(
            @RequestBody @Valid ActivationRequest request
    ) {
        return ResponseEntity.ok(authService.activateUser(request));
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseApiResponse<Map<String, Object>>> loginUser(
            @RequestBody @Valid LoginRequest loginRequest
    ) {
            return ResponseEntity.ok(authService.loginUser(loginRequest));
    }

    @PostMapping(value = "/refresh-token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseApiResponse<Map<String, Object>>> refreshToken(
            @RequestBody @Valid RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/users/me")
    public ResponseEntity<BaseApiResponse<UserResponse>> getCurrentUsersProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {
        String keycloakId = jwt.getClaimAsString("sub");
        return ResponseEntity.ok(authService.getCurrentUser(keycloakId));
    }

    @GetMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<UserResponse>> getUserById(
            @Valid @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(authService.getUserById(userId));
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @DeleteMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<String>> deleteUserById(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(authService.deleteUser(userId));
    }

    @PatchMapping("/admin/users/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<UserResponse>> updateUserRole(
            @RequestBody @Valid UpdateRoleRequest request
    ) {
        return ResponseEntity.ok(authService.updateUserRole(request));
    }

    @PatchMapping("/admin/users/access")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseApiResponse<UserResponse>> updateUserAccess(
            @RequestBody @Valid UpdateAccessRequest request
    ) {
        return ResponseEntity.ok(authService.updateUserAccess(request));
    }

}
