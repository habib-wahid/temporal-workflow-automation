package com.saga;

import com.saga.common.model.PaymentEvent;
import com.saga.common.constants.PaymentEventType;
import com.saga.config.RabbitConfig;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Component
public class PaymentService {
    
    @Autowired 
    private RabbitTemplate rabbitTemplate;

    @Value("${payment.simulation.success}")
    private boolean simulateSuccess;

    @RabbitListener(queues = "payment-events")
    public void consumeEvent(PaymentEvent event) {
        if (PaymentEventType.PROCESS_PAYMENT.matches(event.getEventType())) {
            System.out.println("Processing payment for order: " + event.getOrderId());
            System.out.println("Payment amount: " + event.getAmount() + " " + event.getCurrency());
            
            // Simulate payment processing with configurable success/failure
            if (simulateSuccess) {
                System.out.println("Payment successful for order: " + event.getOrderId());
                PaymentEvent successEvent = new PaymentEvent(
                    event.getOrderId(), 
                    PaymentEventType.PAYMENT_COMPLETED,
                    event.getAmount(),
                    event.getCurrency(),
                    event.getPaymentMethod()
                );
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, RabbitConfig.SAGA_RESPONSE_ROUTING_KEY, successEvent);
            } else {
                System.out.println("Payment failed for order: " + event.getOrderId());
                PaymentEvent failureEvent = new PaymentEvent(
                    event.getOrderId(), 
                    PaymentEventType.PAYMENT_FAILED
                );
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, RabbitConfig.SAGA_RESPONSE_ROUTING_KEY, failureEvent);
            }
        } else if (PaymentEventType.COMPENSATE_PAYMENT.matches(event.getEventType())) {
            System.out.println("Compensating payment for order: " + event.getOrderId());
            // Implement payment refund logic here
            PaymentEvent compensatedEvent = new PaymentEvent(
                event.getOrderId(), 
                PaymentEventType.PAYMENT_COMPENSATED
            );
            rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, RabbitConfig.SAGA_RESPONSE_ROUTING_KEY, compensatedEvent);
        }
    }
}

