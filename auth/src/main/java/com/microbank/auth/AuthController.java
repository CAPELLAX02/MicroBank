package com.microbank.auth;

import com.microbank.auth.dto.request.RegisterRequest;
import com.microbank.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

}
