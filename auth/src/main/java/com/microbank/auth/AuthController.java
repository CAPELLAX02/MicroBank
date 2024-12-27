package com.microbank.auth;

import com.microbank.auth.dto.request.ActivationRequest;
import com.microbank.auth.dto.request.LoginRequest;
import com.microbank.auth.dto.request.RegisterRequest;
import com.microbank.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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

}