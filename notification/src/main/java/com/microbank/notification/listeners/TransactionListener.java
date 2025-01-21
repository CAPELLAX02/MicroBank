package com.microbank.notification.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microbank.notification.event.TransactionEvent;
import com.microbank.notification.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    private final MailService mailService;
    private final ObjectMapper objectMapper;

    public TransactionListener(MailService mailService, ObjectMapper objectMapper) {
        this.mailService = mailService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "transaction-queue")
    public void handleTransactionMessage(String message) {
        try {
            TransactionEvent event = objectMapper.readValue(message, TransactionEvent.class);

            mailService.sendTransactionMail(
                    event.senderAccountEmail(),
                    event.transactionId(),
                    event.senderAccountOwnerName(),
                    event.receiverAccountOwnerName(),
                    event.senderAccountIban(),
                    event.receiverAccountIban(),
                    event.amount(),
                    event.description(),
                    event.timestamp()
            );

            mailService.sendTransactionMail(
                    event.receiverAccountEmail(),
                    event.transactionId(),
                    event.senderAccountOwnerName(),
                    event.receiverAccountOwnerName(),
                    event.senderAccountIban(),
                    event.receiverAccountIban(),
                    event.amount(),
                    event.description(),
                    event.timestamp()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error while processing transaction message", e);
        }
    }


}