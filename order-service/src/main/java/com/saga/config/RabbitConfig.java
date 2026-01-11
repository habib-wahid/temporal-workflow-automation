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
    public static final String ORDER_QUEUE = "order-events";

    // Routing Keys
    public static final String ORDER_ROUTING_KEY = "order.events";

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true); // durable queue
    }

    @Bean
    public Binding orderBinding(Queue orderQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(orderQueue).to(sagaExchange).with(ORDER_ROUTING_KEY);
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

