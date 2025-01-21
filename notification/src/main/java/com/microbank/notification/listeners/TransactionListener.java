package com.microbank.notification.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microbank.notification.TransactionEvent;
import com.microbank.notification.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.MailSender;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final MailSender mailSender;
    private final MailService mailService;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    public TransactionListener(MailSender mailSender, MailService mailService, ObjectMapper objectMapper) {
        this.mailSender = mailSender;
        this.mailService = mailService;
        this.objectMapper = objectMapper;
    }

//    @RabbitListener(queues = "transaction-queue")
//    public void handleTransactionMessage(String message) {
//        logger.info("Raw message from RabbitMQ: {}", message);
//
//        try {
//            TransactionEvent event = objectMapper.readValue(message, TransactionEvent.class);
//
//            logger.info("Parsed Transaction Event: {}", event);
//
//            mailService.sendTransactionMail(
//                    event.sourceAccountEmail(),
//                    event.sourceAccountIBAN(),
//                    event.targetAccountIBAN(),
//                    event.sourceName(),
//                    event.targetName(),
//                    event.amount(),
//                    event.timestamp()
//            );
//
//            mailService.sendTransactionMail(
//                    event.targetAccountEmail(),
//                    event.sourceAccountIBAN(),
//                    event.targetAccountIBAN(),
//                    event.sourceName(),
//                    event.targetName(),
//                    event.amount(),
//                    event.timestamp()
//            );
//        } catch (Exception e) {
//            logger.error("Error while processing transaction message", e);
//        }
//    }

}
