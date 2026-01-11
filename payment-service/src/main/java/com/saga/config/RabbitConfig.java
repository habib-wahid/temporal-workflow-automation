package com.saga.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Exchange
    public static final String SAGA_EXCHANGE = "saga-exchange";

    // Queues
    public static final String PAYMENT_QUEUE = "payment-events";
    public static final String SAGA_PAYMENT_RESPONSE_QUEUE = "saga-payment-response-events";

    // Routing Keys
    public static final String PAYMENT_ROUTING_KEY = "payment.events";
    public static final String SAGA_RESPONSE_ROUTING_KEY = "saga.response.events";

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE);
    }

    @Bean
    public Queue paymentQueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }

    @Bean
    public Queue sagaResponseQueue() {
        return new Queue(SAGA_PAYMENT_RESPONSE_QUEUE, true);
    }

    @Bean
    public Binding paymentBinding(Queue paymentQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(paymentQueue).to(sagaExchange).with(PAYMENT_ROUTING_KEY);
    }

    @Bean
    public Binding sagaResponseBinding(Queue sagaResponseQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(sagaResponseQueue).to(sagaExchange).with(SAGA_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setExchange(SAGA_EXCHANGE);
        return rabbitTemplate;
    }
}

