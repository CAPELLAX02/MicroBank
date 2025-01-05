package com.microbank.auth.controller;

import com.microbank.auth.dto.request.ActivationRequest;
import com.microbank.auth.dto.request.LoginRequest;
import com.microbank.auth.dto.request.RefreshTokenRequest;
import com.microbank.auth.dto.request.RegisterRequest;
import com.microbank.auth.dto.response.UserResponse;
import com.microbank.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<UserResponse> getUserByKeycloakId(
            @PathVariable String keycloakId
    ) {
        return ResponseEntity.ok(authService.getUserByKeycloakId(keycloakId));
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(
            @Validated @RequestBody RegisterRequest request
    ) {
        String message = authService.registerUser(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateUser(
            @Validated @RequestBody ActivationRequest request
    ) {
        String message = authService.activateUser(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginUser(
            @Validated LoginRequest loginRequest
    ) {
        try {
            Map<String, Object> tokens = authService.loginUser(loginRequest);
            return ResponseEntity.ok(tokens);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
//            @Validated @RequestBody RefreshTokenRequest request
            @Validated RefreshTokenRequest request
    ) {
        try {
            Map<String, Object> tokens = authService.refreshToken(request);
            return ResponseEntity.ok(tokens);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @Valid @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(authService.getUserById(userId));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUserById(
            @PathVariable UUID userId
    ) {
        authService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // TODO: Create another controller named "UserController".

}
