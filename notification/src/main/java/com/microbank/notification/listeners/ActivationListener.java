package com.microbank.notification.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microbank.notification.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ActivationListener {

    private final MailService mailService;
    private final ObjectMapper objectMapper;

    public ActivationListener(MailService mailService, ObjectMapper objectMapper) {
        this.mailService = mailService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "activation-queue")
    public void handleActivationMessage(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String email = jsonNode.get("email").asText();
            String firstName = jsonNode.get("firstName").asText();
            String lastName = jsonNode.get("lastName").asText();
            String activationCode = jsonNode.get("activationCode").asText();

            mailService.sendActivationMail(email, firstName, lastName, activationCode);

        } catch (Exception e) {
            throw new RuntimeException("Error while processing activation message", e);
        }
    }

}
