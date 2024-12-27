package com.microbank.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microbank.auth.UserRepository;
import com.microbank.auth.dto.request.RegisterRequest;
import com.microbank.auth.service.AuthService;
import org.keycloak.admin.client.Keycloak;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final Keycloak keycloak;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    public AuthServiceImpl(UserRepository userRepository, Keycloak keycloak, RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper, PasswordEncoder passwordEncoder, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.keycloak = keycloak;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
    }

//    @Value("${keycloak.login.token-url}")
//    private String keycloakLoginUrl;
//
//    @Value("${keycloak.login.grant_type}")
//    private String keycloakLoginGrantType;
//
//    @Value("${keycloak.login.client_id}")
//    private String keycloakLoginClientId;
//
//    @Value("${keycloak.login.client_secret}")
//    private String keycloakLoginClientSecret;

    @Override
    public String registerUser(RegisterRequest request) {
        String activationCode = generateActivationCode();
        saveUserToRedis(request, activationCode);

        Map<String, String> message = new HashMap<>();
        message.put("email", request.email());
        message.put("firstName", request.firstName());
        message.put("lastName", request.lastName());
        message.put("activationCode", activationCode);

        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend("activation-queue", jsonMessage);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to RabbitMQ", e);
        }

        return "User registered successfully. Check your email for the activation code.";
    }

    @Override
    public void saveUserToRedis(RegisterRequest request, String activationCode) {
        try {
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", UUID.randomUUID().toString());
            userData.put("username", request.username());
            userData.put("firstName", request.firstName());
            userData.put("lastName", request.lastName());
            userData.put("email", request.email());
            userData.put("password", request.password());
            userData.put("activationCode", activationCode);

            redisTemplate.opsForValue().set(request.email(),
                    objectMapper.writeValueAsString(userData),
                    Duration.ofMinutes(10));

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error saving user data to Redis", e);
        }
    }

    @Override
    public String generateActivationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

}
