package com.microbank.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microbank.auth.dto.request.ActivationRequest;
import com.microbank.auth.dto.request.LoginRequest;
import com.microbank.auth.dto.request.RegisterRequest;
import com.microbank.auth.dto.response.UserResponse;
import com.microbank.auth.model.User;
import com.microbank.auth.model.enums.UserRole;
import com.microbank.auth.repository.UserRepository;
import com.microbank.auth.service.AuthService;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

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

    @Value("${keycloak.login.token-url}")
    private String keycloakLoginUrl;

    @Value("${keycloak.login.grant-type}")
    private String keycloakLoginGrantType;

    @Value("${keycloak.login.client-id}")
    private String keycloakLoginClientId;

    @Value("${keycloak.login.client-secret}")
    private String keycloakLoginClientSecret;

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

    @Override
    public String activateUser(ActivationRequest request) {
        try {
            String userDataJson = redisTemplate.opsForValue().get(request.email());
            if (userDataJson == null) {
                throw new RuntimeException("Activation code expired or invalid.");
            }

            Map<String, Object> userData = objectMapper.readValue(userDataJson, new TypeReference<>() {});
            if (!request.activationCode().equals(userData.get("activationCode"))) {
                throw new RuntimeException("Invalid activation code.");
            }

            UsersResource usersResource = keycloak.realm("microbank").users();
            UserRepresentation user = new UserRepresentation();
            user.setUsername((String) userData.get("username"));
            user.setFirstName((String) userData.get("firstName"));
            user.setLastName((String) userData.get("lastName"));
            user.setEmail((String) userData.get("email"));
            user.setEnabled(true);
            user.setEmailVerified(true);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue((String) userData.get("password"));
            credential.setTemporary(false);
            user.setCredentials(Collections.singletonList(credential));

            Response response = usersResource.create(user);
            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed to create user in Keycloak. Status: " + response.getStatus());
            }

            String locationHeader = response.getHeaderString("Location");
            String keycloakId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

            RoleRepresentation userRole = keycloak.realm("microbank")
                    .roles()
                    .get("USER")
                    .toRepresentation();

            usersResource.get(keycloakId).roles().realmLevel().add(Collections.singletonList(userRole));

            User dbUser = new User();
            dbUser.setKeycloakId(keycloakId);
            dbUser.setUsername(userData.get("username").toString());
            dbUser.setFirstName((String) userData.get("firstName"));
            dbUser.setLastName((String) userData.get("lastName"));
            dbUser.setEmail((String) userData.get("email"));
            dbUser.setPassword(passwordEncoder.encode((String) userData.get("password")));
            dbUser.setActivated(true);
            dbUser.setRole(UserRole.USER);
            dbUser.setActivationCode(null);

            userRepository.save(dbUser);

            redisTemplate.delete(request.email());

            return "User activated and registered successfully.";

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing user data from Redis", e);
        }
    }



    public Map<String, Object> loginUser(LoginRequest loginRequest) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", keycloakLoginGrantType);
            requestBody.add("client_id", keycloakLoginClientId);
            requestBody.add("client_secret", keycloakLoginClientSecret);
            requestBody.add("username", loginRequest.username());
            requestBody.add("password", loginRequest.password());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(keycloakLoginUrl, HttpMethod.POST, requestEntity, Map.class);

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    @Override
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getKeycloakId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail()
        );
    }

    @Override
    public UserResponse getUserByKeycloakId(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found with Keycloak ID: " + keycloakId));

        return new UserResponse(
                user.getId(),
                user.getKeycloakId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail()
        );
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            userResponses.add(new UserResponse(
                    user.getId(),
                    user.getKeycloakId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getUsername(),
                    user.getEmail()
            ));
        }
        return userResponses;
    }

    @Override
    public void deleteUser(UUID id) {
        // TODO: Also delete user from Keycloak realm permanently.
        userRepository.deleteById(id);
    }

}
