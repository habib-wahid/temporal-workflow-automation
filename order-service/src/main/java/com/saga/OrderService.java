package com.saga;

import com.saga.common.constants.InventoryEventType;
import com.saga.common.model.InventoryEvent;
import com.saga.common.model.OrderEvent;
import com.saga.common.constants.OrderEventType;
import com.saga.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


@Component
public class OrderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${order.simulation.success}")
    private boolean simulateSuccess;

    @RabbitListener(queues = "order-events")
    public void consumeEvent(OrderEvent event) {
        if (OrderEventType.ORDER_CREATED.matches(event.getEventType())) {

            System.out.println("Reserving inventory for order: " + event.getOrderId());

            // Simulate inventory reservation with configurable success/failure
            if (simulateSuccess) {
                System.out.println("Inventory reserved successfully for order: " + event.getOrderId());

                OrderEvent orderEvent = new OrderEvent(
                        event.getOrderId(),
                        OrderEventType.ORDER_CONFIRMED
                );

                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, RabbitConfig.ORDER_ROUTING_KEY, orderEvent);
            } else {
                System.out.println("Inventory reservation failed for order: " + event.getOrderId());
                OrderEvent failedEvent = new OrderEvent(
                        event.getOrderId(),
                        OrderEventType.FAILED_ORDER
                );
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, RabbitConfig.ORDER_ROUTING_KEY, failedEvent);
            }
        }
    }
}

