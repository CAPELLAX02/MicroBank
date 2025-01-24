package com.microbank.notification.service;

import jakarta.mail.MessagingException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface MailService {

    void sendActivationMail(
            String to,
            String firstName,
            String lastName,
            String activationCode
    ) throws MessagingException;

    void sendTransactionMail(
            String to,
            UUID transactionId,
            String senderName,
            String receiverName,
            String senderIban,
            String receiverIban,
            BigDecimal amount,
            String description,
            LocalDateTime timestamp
    ) throws MessagingException;

    void sendPasswordRecoveryMail(
            String to,
            String passwordRecoveryCode
    ) throws MessagingException;
}
