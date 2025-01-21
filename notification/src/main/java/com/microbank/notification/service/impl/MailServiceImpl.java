package com.microbank.notification.service.impl;

import com.microbank.notification.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${sender-email}")
    private String senderEmail;

    public MailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendActivationMail(String to, String firstName, String lastName, String activationCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Context context = new Context();
        context.setVariable("firstName", firstName);
        context.setVariable("lastName", lastName);
        context.setVariable("activationCode", activationCode);

        String content = templateEngine.process("activation-email", context);

        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject("Account Activation Code");
        helper.setText(content, true);

        mailSender.send(message);
    }

//    @Override
//    public void sendTransactionMail(String to, String sourceName, String targetName, String sourceIBAN, String targetIBAN, BigDecimal amount, LocalDateTime timestamp) throws MessagingException {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//        Context context = new Context();
//        context.setVariable("sourceName", sourceName);
//        context.setVariable("targetName", targetName);
//        context.setVariable("sourceIBAN", sourceIBAN);
//        context.setVariable("targetIBAN", targetIBAN);
//        context.setVariable("amount", amount);
//        context.setVariable("timestamp", timestamp);
//
//        String content = templateEngine.process("transaction-email", context);
//
//        helper.setFrom(senderEmail);
//        helper.setTo(to);
//        helper.setSubject("Transaction Receipt");
//        helper.setText(content, true);
//
//        mailSender.send(message);
//    }
//
//    private String formatTimestamp(LocalDateTime timestamp) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, hh:mm a");
//        return timestamp.format(formatter);
//    }

    @Override
    public void sendTransactionMail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }

}
