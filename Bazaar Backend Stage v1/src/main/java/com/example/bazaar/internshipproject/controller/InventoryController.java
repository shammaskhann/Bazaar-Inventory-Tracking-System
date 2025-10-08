package com.example.bazaar.internshipproject.controller;

import com.example.bazaar.internshipproject.config.DBConnect;
import com.example.bazaar.internshipproject.entities.Inventory;
import com.example.bazaar.internshipproject.repositories.InventoryRepository;
import com.example.bazaar.internshipproject.repositories.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping( "/api/inventory")
public class InventoryController {

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    StockMovementRepository stockMovementRepository;

    @Autowired
    DBConnect dbConnect;

    @GetMapping
    public ResponseEntity<?> getInventory() {
        try{
            List<Inventory> inventoryList = inventoryRepository.getAlInventory();
            if(inventoryList.isEmpty()){
                return new ResponseEntity<>(
                        Map.of("status", false, "message", "No inventory found")
                , HttpStatus.NOT_FOUND);
            }else{
                return ResponseEntity.ok(Map.of("status", true, "data", inventoryList));
            }
        }catch (Exception e){
            return new ResponseEntity<>(
                    Map.of("status", false, "message", e.getMessage())
            , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/stock-in")
    public ResponseEntity<?> stockIn(@RequestParam Long productId, @RequestParam int quantity) {
        return handleStockMovement(productId, quantity, "STOCK_IN");
    }

    @PostMapping("/sale")
    public ResponseEntity<?> sellProduct(@RequestParam Long productId, @RequestParam int quantity) {
        return handleStockMovement(productId, -quantity, "SALE");
    }
    @PostMapping("/manual-removal")
    public ResponseEntity<?> removeStock(@RequestParam Long productId, @RequestParam int quantity) {
        return handleStockMovement(productId, -quantity, "MANUAL_REMOVAL");
    }

    private ResponseEntity<?> handleStockMovement(Long productId, int quantityChange, String movementType) {
        try {
            Connection connection = dbConnect.getConnection();
            Inventory inventory = inventoryRepository.getInventoryByProductId(productId, connection);

            if (inventory == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", false, "message", "Product not found in inventory"));
            }

            int newQuantity = inventory.getQuantity() + quantityChange;
            if (newQuantity < 0) {
                return ResponseEntity.badRequest().body(Map.of("status", false, "message", "Insufficient stock!"));
            }

            stockMovementRepository.recordStockMovement(productId, movementType, Math.abs(quantityChange), connection);

            inventoryRepository.updateStock(productId, quantityChange, connection);

            return ResponseEntity.ok(Map.of("status", true, "new_quantity", newQuantity, "movement", movementType));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", false, "message", e.getMessage()));
        }
    }


}
