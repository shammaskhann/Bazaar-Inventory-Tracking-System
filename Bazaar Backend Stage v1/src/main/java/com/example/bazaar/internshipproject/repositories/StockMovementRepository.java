package com.example.bazaar.internshipproject.repositories;

import com.example.bazaar.internshipproject.config.DBConnect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class StockMovementRepository {

    private final InventoryRepository inventoryRepository = new InventoryRepository();

    public void recordStockMovement(Long productId, String changeType, int quantity, Connection connection) {
        if (!List.of("STOCK_IN", "SALE", "MANUAL_REMOVAL").contains(changeType)) {
            throw new IllegalArgumentException("Invalid stock movement type");
        }

        String query = "INSERT INTO stock_movements (product_id, change_type, quantity, movement_date) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try {


             PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, productId);
            preparedStatement.setString(2, changeType);
            preparedStatement.setInt(3, quantity);
            preparedStatement.executeUpdate();

            log.info("Stock movement recorded: " + changeType + " for Product ID: " + productId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


