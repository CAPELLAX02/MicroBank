package com.microbank.transaction.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TRANSACTION_REQUESTED_QUEUE = "transaction-requested";
    public static final String TRANSACTION_COMPLETED_QUEUE = "transaction-completed";

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    public Queue transactionRequestedQueue() {
        return new Queue(TRANSACTION_REQUESTED_QUEUE, true);
    }

    @Bean
    public Queue transactionCompletedQueue() {
        return new Queue(TRANSACTION_COMPLETED_QUEUE, true);
    }

    @Bean
    public TopicExchange transactionExchange() {
        return new TopicExchange("transaction-exchange");
    }

    @Bean
    public Binding requestedBinding(Queue transactionRequestedQueue, TopicExchange transactionExchange) {
        return BindingBuilder.bind(transactionRequestedQueue).to(transactionExchange).with("transaction.requested");
    }

    @Bean
    public Binding completedBinding(Queue transactionCompletedQueue, TopicExchange transactionExchange) {
        return BindingBuilder.bind(transactionCompletedQueue).to(transactionExchange).with("transaction.completed");
    }

}
