package com.example.internshipproject.InventoryManagementV2.service;

import com.example.internshipproject.InventoryManagementV2.config.RabbitMQConfig;
import com.example.internshipproject.InventoryManagementV2.entities.StockMovementMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMovementProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendStockMovement(StockMovementMessage message) {
        log.info("Queuing stock movement request: productId={}, storeId={}, quantity={}",
                message.getProduct().getId(), message.getStore().getId(), message.getQuantity());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
        log.info("Stock movement request successfully queued.");
    }
}

