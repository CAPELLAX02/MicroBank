package com.microbank.auth.service;

import com.microbank.auth.dto.request.RegisterRequest;

public interface AuthService {

    String registerUser(RegisterRequest request);
    void saveUserToRedis(RegisterRequest request, String activationCode);
    String generateActivationCode();

}
