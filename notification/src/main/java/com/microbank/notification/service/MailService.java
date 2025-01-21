package com.microbank.notification.service;

import jakarta.mail.MessagingException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface MailService {

    void sendActivationMail(String to, String firstName, String lastName, String activationCode) throws MessagingException;
//    void sendTransactionMail(String to, String sourceName, String targetName, String sourceIBAN, String targetIBAN, BigDecimal amount, LocalDateTime timestamp) throws MessagingException;
    void sendTransactionMail(String to, String subject, String body) throws MessagingException;

}
