package com.saga;

import com.saga.common.model.InventoryEvent;
import com.saga.common.constants.InventoryEventType;
import com.saga.config.RabbitConfig;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Component
public class InventoryService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${inventory.simulation.success}")
    private boolean simulateSuccess;

    @RabbitListener(queues = "inventory-events")
    public void consumeEvent(InventoryEvent event) {
        if (InventoryEventType.RESERVE_INVENTORY.matches(event.getEventType())) {
            System.out.println("Reserving inventory for order: " + event.getOrderId());
            System.out.println("Product: " + event.getProductId() + ", Quantity: " + event.getQuantity());
            
            // Simulate inventory reservation with configurable success/failure
            if (simulateSuccess) {
                System.out.println("Inventory reserved successfully for order: " + event.getOrderId());
                InventoryEvent reservedEvent = new InventoryEvent(
                    event.getOrderId(), 
                    InventoryEventType.INVENTORY_RESERVED,
                    event.getProductId(),
                    event.getQuantity(),
                    event.getWarehouseId()
                );
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, RabbitConfig.SAGA_RESPONSE_ROUTING_KEY, reservedEvent);
            } else {
                System.out.println("Inventory reservation failed for order: " + event.getOrderId());
                InventoryEvent failedEvent = new InventoryEvent(
                    event.getOrderId(), 
                    InventoryEventType.INVENTORY_FAILED
                );
                rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, RabbitConfig.SAGA_RESPONSE_ROUTING_KEY, failedEvent);
            }
        } else if (InventoryEventType.COMPENSATE_INVENTORY.matches(event.getEventType())) {
            System.out.println("Compensating inventory reservation for order: " + event.getOrderId());
            // Implement inventory release logic here
            InventoryEvent compensatedEvent = new InventoryEvent(
                event.getOrderId(), 
                InventoryEventType.INVENTORY_COMPENSATED
            );
            rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, RabbitConfig.SAGA_RESPONSE_ROUTING_KEY, compensatedEvent);
        }
    }
}
