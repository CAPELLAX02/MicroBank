package com.microbank.notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue activationQueue() {
        return new Queue("activation-queue", true);
    }

    // TODO: Implement "reset-password-queue".

    @Bean
    public Queue transactionQueue() {
        return new Queue("transaction-queue", true);
    }

}
