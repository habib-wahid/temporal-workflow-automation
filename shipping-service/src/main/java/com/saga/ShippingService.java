package com.saga;

import com.saga.common.model.ShippingEvent;
import com.saga.common.constants.ShippingEventType;
import com.saga.config.RabbitConfig;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Component
public class ShippingService {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${shipping.simulation.success}")
    private boolean simulateSuccess;

    @RabbitListener(queues = "shipping-events")
    public void consumeEvent(ShippingEvent event) {
        if (ShippingEventType.PROCESS_SHIPPING.matches(event.getEventType())) {
            System.out.println("Processing shipping for order: " + event.getOrderId());
            System.out.println("Shipping address: " + event.getShippingAddress());
                
            if (simulateSuccess) {
                System.out.println("Shipping completed successfully for order: " + event.getOrderId());
                ShippingEvent completedEvent = new ShippingEvent(
                    event.getOrderId(), 
                    ShippingEventType.SHIPPING_COMPLETED,
                    event.getShippingAddress(),
                    "TRACK-" + event.getOrderId(), // Generate tracking number
                    "Standard Carrier"
                );
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, RabbitConfig.SAGA_RESPONSE_ROUTING_KEY, completedEvent);
            } else {
                System.out.println("Shipping failed for order: " + event.getOrderId());
                ShippingEvent failedEvent = new ShippingEvent(
                    event.getOrderId(), 
                    ShippingEventType.SHIPPING_FAILED
                );
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, RabbitConfig.SAGA_RESPONSE_ROUTING_KEY, failedEvent);
            }
        } else if (ShippingEventType.COMPENSATE_SHIPPING.matches(event.getEventType())) {
            System.out.println("Compensating shipping for order: " + event.getOrderId());
            ShippingEvent compensatedEvent = new ShippingEvent(
                event.getOrderId(), 
                ShippingEventType.SHIPPING_COMPENSATED
            );
            rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, RabbitConfig.SAGA_RESPONSE_ROUTING_KEY, compensatedEvent);
        }
    }
} 