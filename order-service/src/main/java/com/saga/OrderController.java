package com.saga;

import com.saga.common.model.OrderEvent;
import com.saga.common.constants.OrderEventType;
import com.saga.config.RabbitConfig;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("/{orderId}")
    public String createOrder(
            @PathVariable String orderId,
            @RequestParam(required = false) String customerId) {
        System.out.println("Creating order: " + orderId);
        OrderEvent event = new OrderEvent(orderId, OrderEventType.ORDER_CREATED, customerId);
        rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, RabbitConfig.ORDER_ROUTING_KEY, event);
        return "Order Creation Request is Initiated for order: " + orderId +
               (customerId != null ? " (Customer: " + customerId + ")" : "");
    }
}

