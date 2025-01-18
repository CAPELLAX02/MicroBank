package com.microbank.auth.service.utils;

import com.microbank.auth.dto.response.UserResponse;
import com.microbank.auth.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserServiceUtils {

    public UserResponse buildUserResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getKeycloakId(),
                u.getUsername(),
                u.getEmail(),
                u.getFirstName(),
                u.getLastName()
        );
    }

    public List<UserResponse> buildUserResponses(List<User> users) {
        List<UserResponse> userResponses = new ArrayList<>();
        for (User u : users) {
            userResponses.add(buildUserResponse(u));
        }
        return userResponses;
    }

}
