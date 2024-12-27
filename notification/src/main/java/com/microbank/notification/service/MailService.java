package com.microbank.notification.service;

import jakarta.mail.MessagingException;

public interface MailService {

    void sendActivationMail(String to, String firstName, String lastName, String activationCode) throws MessagingException;

}
