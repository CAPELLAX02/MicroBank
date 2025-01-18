package com.microbank.transaction.listener;

import com.microbank.transaction.dto.event.TransactionCompletedEvent;
import com.microbank.transaction.dto.event.TransactionRequestedEvent;
import com.microbank.transaction.dto.request.UpdateBalanceRequest;
import com.microbank.transaction.feign.AccountServiceClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class TransactionRequestedListener {
    private final AccountServiceClient accountServiceClient;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionRequestedListener(
            AccountServiceClient accountServiceClient,
            ApplicationEventPublisher eventPublisher
    ) {
        this.accountServiceClient = accountServiceClient;
        this.eventPublisher = eventPublisher;
    }

    @RabbitListener(queues = "transaction-requested")
    public void handleTransactionRequested(TransactionRequestedEvent event) {
        try {
            accountServiceClient.updateAccountBalance(new UpdateBalanceRequest(
                    event.receiverAccountId(),
                    event.amount(),
                    true
            ));

            TransactionCompletedEvent completedEvent = new TransactionCompletedEvent(
                    event.transactionId(),
                    event.receiverAccountId(),
                    event.amount(),
                    "SUCCESS"
            );
            eventPublisher.publishEvent(completedEvent);

        } catch (Exception e) {
            TransactionCompletedEvent failedEvent = new TransactionCompletedEvent(
                    event.transactionId(),
                    event.receiverAccountId(),
                    event.amount(),
                    "FAILED"
            );
            eventPublisher.publishEvent(failedEvent);
        }
    }
}
