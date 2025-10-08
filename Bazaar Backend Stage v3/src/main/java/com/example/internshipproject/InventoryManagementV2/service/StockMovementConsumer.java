package com.example.internshipproject.InventoryManagementV2.service;

import com.example.internshipproject.InventoryManagementV2.config.RabbitMQConfig;
import com.example.internshipproject.InventoryManagementV2.entities.StockMovementMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMovementConsumer {

    @Autowired
    private final StockMovementService stockMovementService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consume(StockMovementMessage message) {
        log.info("Received stock movement message: productId={}, storeId={}, quantity={}",
                message.getProduct().getId(), message.getStore().getId(), message.getQuantity());
     try{
         stockMovementService.processStockMovement(message);  // This will write to DB
         log.info("Stock movement processed successfully: productId={}, storeId={}",
                 message.getProduct().getId(), message.getStore().getId());
     }catch (Exception e){
         log.error("Failed to process stock movement: productId={}, storeId={}, error={}",
                 message.getProduct().getId(), message.getStore().getId(), e.getMessage());
     }
    }
}
