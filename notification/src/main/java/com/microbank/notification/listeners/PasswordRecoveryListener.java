package com.microbank.notification.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microbank.notification.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PasswordRecoveryListener {

    private final MailService mailService;
    private final ObjectMapper objectMapper;

    public PasswordRecoveryListener(MailService mailService, ObjectMapper objectMapper) {
        this.mailService = mailService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "password-recovery-queue")
    public void handlePasswordRecovery(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String email = jsonNode.get("email").asText();
            String passwordRecoveryCode = jsonNode.get("passwordRecoveryCode").asText();

            mailService.sendPasswordRecoveryMail(email, passwordRecoveryCode);

        } catch (Exception e) {
            throw new RuntimeException("Error while processing password recovery message", e);
        }
    }

}
