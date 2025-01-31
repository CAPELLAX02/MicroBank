package com.microbank.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microbank.auth.dto.request.*;
import com.microbank.auth.dto.response.UserResponse;
import com.microbank.auth.exception.CustomException;
import com.microbank.auth.exception.NotFoundException;
import com.microbank.auth.exception.UnauthorizedException;
import com.microbank.auth.model.User;
import com.microbank.auth.model.enums.UserRole;
import com.microbank.auth.repository.UserRepository;
import com.microbank.auth.response.BaseApiResponse;
import com.microbank.auth.service.AuthService;
import com.microbank.auth.service.utils.UserServiceUtils;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
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
    private final RestTemplate restTemplate;
    private final UserServiceUtils userServiceUtils;

    public AuthServiceImpl(
            UserRepository userRepository,
            Keycloak keycloak,
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            PasswordEncoder passwordEncoder,
            RabbitTemplate rabbitTemplate,
            RestTemplate restTemplate,
            UserServiceUtils userServiceUtils
    ) {
        this.userRepository = userRepository;
        this.keycloak = keycloak;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = restTemplate;
        this.userServiceUtils = userServiceUtils;
    }

    @Value("${keycloak.login.token-url}")
    private String keycloakLoginUrl;

    @Value("${keycloak.login.grant-type}")
    private String keycloakLoginGrantType;

    @Value("${keycloak.refresh-token.grant-type}")
    private String keycloakRefreshTokenGrantType;

    @Value("${keycloak.login.client-id}")
    private String keycloakLoginClientId;

    @Value("${keycloak.login.client-secret}")
    private String keycloakLoginClientSecret;

    @Override
    public BaseApiResponse<String> registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return new BaseApiResponse<>(
                    HttpStatus.CONFLICT.value(),
                    "User with the same username (" + request.username() + ") already exists",
                    null
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            return new BaseApiResponse<>(
                    HttpStatus.CONFLICT.value(),
                    "User with the same email (" + request.email() + ") already exists",
                    null
            );
        }

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
            return new BaseApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to send activation email.",
                    null
            );
        }

        return new BaseApiResponse<>(
                HttpStatus.CREATED.value(),
                "Registration successful, activation code sent to " + request.email(),
                null
        );
    }

    private void saveUserToRedis(RegisterRequest request, String activationCode) {
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
            throw new CustomException("Error saving user data to Redis");
        }
    }

    private String generateActivationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private String generatePasswordRecoveryCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    @Override
    public BaseApiResponse<String> activateUser(ActivationRequest request) {
        try {
            String userDataJson = redisTemplate.opsForValue().get(request.email());
            if (userDataJson == null) {
                return new BaseApiResponse<>(
                        HttpStatus.NOT_FOUND.value(),
                        "Activation code expired or invalid.",
                        null
                );
            }

            Map<String, Object> userData = objectMapper.readValue(userDataJson, new TypeReference<>() {});
            if (!request.activationCode().equals(userData.get("activationCode"))) {
                return new BaseApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid or expired activation code.",
                        null
                );
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
                throw new CustomException("Failed to create user in Keycloak. Status: " + response.getStatus());
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
            dbUser.setBanned(false);

            userRepository.save(dbUser);

            redisTemplate.delete(request.email());

            return new BaseApiResponse<>(
                    HttpStatus.OK.value(),
                    "Account activation successful, you can login to the system.",
                    null
            );

        } catch (JsonProcessingException e) {
            return new BaseApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error processing user data from Redis.",
                    null
            );
        }
    }

    public BaseApiResponse<Map<String, Object>> loginUser(LoginRequest loginRequest) {
        try {
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", keycloakLoginGrantType);
            requestBody.add("client_id", keycloakLoginClientId);
            requestBody.add("client_secret", keycloakLoginClientSecret);
            requestBody.add("username", loginRequest.username());
            requestBody.add("password", loginRequest.password());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    keycloakLoginUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {}
            );

            // TODO: "Banned" users cannot login logic goes here later on

            return new BaseApiResponse<>(HttpStatus.OK.value(), "Login successful.", response.getBody());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new UnauthorizedException("Invalid username or password.");
            }

            return new BaseApiResponse<>(
                    e.getStatusCode().value(),
                    "Login failed: " + e.getStatusText(),
                    null
            );

        } catch (Exception e) {
            return new BaseApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Login failed due to server error: " + e.getMessage(),
                    null
            );
        }
    }

    public BaseApiResponse<Map<String, Object>> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", keycloakRefreshTokenGrantType);
            requestBody.add("client_id", keycloakLoginClientId);
            requestBody.add("client_secret", keycloakLoginClientSecret);
            requestBody.add("refresh_token", refreshTokenRequest.refreshToken());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            System.out.println("Headers: " + headers);
            System.out.println("Request Body: " + requestBody);
            System.out.println("Keycloak URL: " + keycloakLoginUrl);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    keycloakLoginUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<>() {}
            );

            return new BaseApiResponse<>(HttpStatus.OK.value(), "Token refreshed successfully.", response.getBody());

        } catch (Exception e) {
            return new BaseApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Token refresh failed: " + Arrays.toString(e.getStackTrace()), null);
        }
    }

    @Override
    public BaseApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new NotFoundException("User with email " + request.email() + " not found"));

        String passwordRecoveryCode = generatePasswordRecoveryCode();

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId().toString());
        userData.put("email", normalizedEmail);
        userData.put("passwordRecoveryCode", passwordRecoveryCode);

        String redisKey = "forgot-password:" + normalizedEmail;
        try {
            redisTemplate.opsForValue().set(
                    redisKey,
                    objectMapper.writeValueAsString(userData),
                    Duration.ofMinutes(15)
            );

        } catch (JsonProcessingException e) {
            throw new CustomException("Error saving recovery data to Redis");
        }

        Map<String, String> message = new HashMap<>();
        message.put("email", normalizedEmail);
        message.put("passwordRecoveryCode", passwordRecoveryCode);

        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend("password-recovery-queue", jsonMessage);

        } catch (Exception e) {
            return new BaseApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "An error occurred while sending password recovery message.",
                    null
            );
        }

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Password recovery code sent to " + request.email(),
                null
        );
    }

    @Override
    public BaseApiResponse<UserResponse> resetPassword(ResetPasswordRequest request) {
        String redisKey = "forgot-password:" + request.email().trim().toLowerCase();
        String userDataJson = redisTemplate.opsForValue().get(redisKey);

        if (userDataJson == null) {
            throw new CustomException("Invalid or expired password recovery code.");
        }

        try {
            Map<String, Object> userData = objectMapper.readValue(userDataJson, new TypeReference<>() {});
            String storedCode = (String) userData.get("passwordRecoveryCode");

            if (storedCode == null || !storedCode.trim().equals(request.passwordRecoveryCode().trim())) {
                throw new CustomException("Invalid or expired password recovery code.");
            }

            UUID userId = UUID.fromString((String) userData.get("id"));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

            UsersResource usersResource = keycloak.realm("microbank").users();
            usersResource.get(user.getKeycloakId()).resetPassword(createCredentialRepresentation(request.newPassword()));
            usersResource.get(user.getKeycloakId()).logout();

            user.setPassword(passwordEncoder.encode(request.newPassword()));
            userRepository.save(user);

            new Thread(() -> redisTemplate.delete(redisKey)).start();

            UserResponse userResponse = userServiceUtils.buildUserResponse(user);
            return new BaseApiResponse<>(
                    HttpStatus.OK.value(),
                    "Password reset successfully. You can log back into the system.",
                    userResponse
            );

        } catch (JsonProcessingException e) {
            return new BaseApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error processing recovery data from Redis: " + e.getMessage(),
                    null
            );
        } catch (Exception e) {
            return new BaseApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "An unexpected error occurred while resetting the password: " + e.getMessage(),
                    null
            );
        }
    }

    private CredentialRepresentation createCredentialRepresentation(String newPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(false);
        return credential;
    }

    @Override
    public BaseApiResponse<UserResponse> getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "User with the ID: " + userId + " retrieved successfully.",
                userServiceUtils.buildUserResponse(user)
        );
    }

    private UserResponse getUserByKeycloakId(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found with Keycloak ID: " + keycloakId));

        return userServiceUtils.buildUserResponse(user);
    }

    @Override
    public BaseApiResponse<UserResponse> getCurrentUser(String keycloakId) {
        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Current user's profile retrieved successfully.",
                getUserByKeycloakId(keycloakId)
        );
    }

    @Override
    public BaseApiResponse<List<UserResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "All users retrieved successfully.",
                userServiceUtils.buildUserResponses(users)
        );
    }

    @Override
    public BaseApiResponse<String> deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        UsersResource usersResource = keycloak.realm("microbank").users();

        try {
            usersResource.get(user.getKeycloakId()).remove();
        } catch (Exception e) {
            return new BaseApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error deleting user from Keycloak: " + e.getMessage(),
                    null
            );
        }

        userRepository.deleteById(userId);

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "User with the ID: " + userId + " deleted successfully.",
                null
        );
    }

    @Override
    public BaseApiResponse<UserResponse> updateUserRole(UpdateRoleRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + request.userId()));

        String oldRole = user.getRole().name();
        String newRole = request.newRole().toUpperCase();

        UsersResource usersResource = keycloak.realm("microbank").users();
        List<RoleRepresentation> assignedRoles = usersResource.get(user.getKeycloakId())
                .roles()
                .realmLevel()
                .listAll();

        List<RoleRepresentation> rolesToRemove = assignedRoles.stream()
                .filter(role -> !role.getName().equals("default-roles-microbank"))
                .toList();

        if (!rolesToRemove.isEmpty()) {
            usersResource.get(user.getKeycloakId())
                    .roles()
                    .realmLevel()
                    .remove(rolesToRemove);
        }

        RoleRepresentation newKeycloakRole = keycloak.realm("microbank")
                .roles()
                .get(newRole)
                .toRepresentation();

        usersResource.get(user.getKeycloakId())
                .roles()
                .realmLevel()
                .add(Collections.singletonList(newKeycloakRole));

        user.setRole(UserRole.valueOf(newRole));
        userRepository.save(user);

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Role of the user with the ID: " + request.userId() + " changed from " + oldRole + " to " + newRole + " successfully.",
                userServiceUtils.buildUserResponse(user)
        );
    }

    @Override
    public BaseApiResponse<UserResponse> updateUserAccess(UpdateAccessRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + request.userId()));

        UsersResource usersResource = keycloak.realm("microbank").users();
        UserRepresentation userRepresentation = usersResource.get(user.getKeycloakId()).toRepresentation();

        userRepresentation.setEnabled(!request.isBanned());
        usersResource.get(user.getKeycloakId()).update(userRepresentation);

        user.setBanned(request.isBanned());
        userRepository.save(user);

        return new BaseApiResponse<>(
                HttpStatus.OK.value(),
                "Access of the user with the ID: " + request.userId() + " updated successfully.",
                userServiceUtils.buildUserResponse(user)
        );
    }

}
