package com.microbank.notification.listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microbank.notification.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Component;

@Component
public class ActivationListener {

    private final MailSender mailSender;
    private final MailService mailService;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(ActivationListener.class);

    public ActivationListener(MailSender mailSender, MailService mailService, ObjectMapper objectMapper) {
        this.mailSender = mailSender;
        this.mailService = mailService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "activation-queue")
    public void handleActivationMessage(String message) {
        logger.info("Received message from RabbitMQ: {}", message);
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String email = jsonNode.get("email").asText();
            String firstName = jsonNode.get("firstName").asText();
            String lastName = jsonNode.get("lastName").asText();
            String activationCode = jsonNode.get("activationCode").asText();

            mailService.sendActivationMail(email, firstName, lastName, activationCode);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
