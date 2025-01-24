package com.microbank.document.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microbank.document.dto.event.TransactionEvent;
import com.microbank.document.exception.CustomException;
import com.microbank.document.service.DocumentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DocumentListener {

    private final DocumentService documentService;
    private final ObjectMapper objectMapper;

    public DocumentListener(DocumentService documentService, ObjectMapper objectMapper) {
        this.documentService = documentService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "transaction-queue")
    public void handleTransactionMessage(String message) {
        try {
            TransactionEvent event = objectMapper.readValue(message, TransactionEvent.class);
            documentService.createTransactionDocumentFromEvent(event);

        } catch (Exception e) {
            throw new CustomException("Error while processing transaction message");
        }
    }

}
