package com.microbank.transaction.listener;

import com.microbank.transaction.dto.event.TransactionCompletedEvent;
import com.microbank.transaction.model.TransactionStatus;
import com.microbank.transaction.repository.TransactionRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionCompletedListener {

    private final TransactionRepository transactionRepository;

    public TransactionCompletedListener(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @RabbitListener(queues = "transaction-completed")
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        transactionRepository.findById(event.transactionId()).ifPresent(transaction -> {
            transaction.setStatus(TransactionStatus.valueOf(event.description()));
            transactionRepository.save(transaction);
        });
    }

}
