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
    public static final String PAYMENT_QUEUE = "payment-events";
    public static final String INVENTORY_QUEUE = "inventory-events";
    public static final String SHIPPING_QUEUE = "shipping-events";
    public static final String SAGA_RESPONSE_QUEUE = "saga-response-events";

    // Routing Keys
    public static final String ORDER_ROUTING_KEY = "order.events";
    public static final String PAYMENT_ROUTING_KEY = "payment.events";
    public static final String INVENTORY_ROUTING_KEY = "inventory.events";
    public static final String SHIPPING_ROUTING_KEY = "shipping.events";
    public static final String SAGA_RESPONSE_ROUTING_KEY = "saga.response.events";

    // Exchange Bean
    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE);
    }

    // Queue Beans
    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true);
    }

    @Bean
    public Queue paymentQueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }

    @Bean
    public Queue inventoryQueue() {
        return new Queue(INVENTORY_QUEUE, true);
    }

    @Bean
    public Queue shippingQueue() {
        return new Queue(SHIPPING_QUEUE, true);
    }

    @Bean
    public Queue sagaResponseQueue() {
        return new Queue(SAGA_RESPONSE_QUEUE, true);
    }

    // Binding Beans
    @Bean
    public Binding orderBinding(Queue orderQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(orderQueue).to(sagaExchange).with(ORDER_ROUTING_KEY);
    }

    @Bean
    public Binding paymentBinding(Queue paymentQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(paymentQueue).to(sagaExchange).with(PAYMENT_ROUTING_KEY);
    }

    @Bean
    public Binding inventoryBinding(Queue inventoryQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(inventoryQueue).to(sagaExchange).with(INVENTORY_ROUTING_KEY);
    }

    @Bean
    public Binding shippingBinding(Queue shippingQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(shippingQueue).to(sagaExchange).with(SHIPPING_ROUTING_KEY);
    }

    @Bean
    public Binding sagaResponseBinding(Queue sagaResponseQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(sagaResponseQueue).to(sagaExchange).with(SAGA_RESPONSE_ROUTING_KEY);
    }

    // Message Converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setExchange(SAGA_EXCHANGE);
        return rabbitTemplate;
    }
}

