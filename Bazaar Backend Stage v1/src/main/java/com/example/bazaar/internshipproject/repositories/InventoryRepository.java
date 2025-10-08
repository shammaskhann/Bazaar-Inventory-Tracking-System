package com.example.bazaar.internshipproject.repositories;


import com.example.bazaar.internshipproject.config.DBConnect;
import com.example.bazaar.internshipproject.entities.Inventory;
import com.example.bazaar.internshipproject.entities.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class InventoryRepository {

//    private final DBConnect dbConnect = new DBConnect();
    @Autowired
    ProductRepository productRepository = new ProductRepository();

    public Inventory getInventoryByProductId(Long productId,Connection connection) throws SQLException {
        String query = "SELECT * FROM inventory WHERE product_id = ?";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Inventory inventory = new Inventory();
                inventory.setId(resultSet.getLong("id"));
                inventory.setProduct(productRepository.getProductById(productId,connection));
                inventory.setQuantity(resultSet.getInt("quantity"));
                inventory.setUpdatedAt(resultSet.getTimestamp("last_updated").toLocalDateTime());
                return inventory;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getStock(Long productId) {
        final DBConnect dbConnect = new DBConnect();
        String query = "SELECT quantity FROM inventory WHERE product_id = ?";
        try (Connection connection = dbConnect.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("quantity");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Inventory> getAlInventory(){
        final DBConnect dbConnect = new DBConnect();
        String query = "SELECT * FROM inventory";
        try (Connection connection = dbConnect.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Inventory> inventories = new ArrayList<>();
            while (resultSet.next()) {
                Inventory inventory = new Inventory();
                inventory.setId(resultSet.getLong("id"));
                Long productId = resultSet.getLong("product_id");
                inventory.setProduct(productRepository.getProductById(productId,connection));
                inventory.setQuantity(resultSet.getInt("quantity"));
                inventory.setUpdatedAt(resultSet.getTimestamp("last_updated").toLocalDateTime());
                inventories.add(inventory);
            }
            return inventories;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateStock(Long productId, int quantityChange,Connection connection) {
        String query = "UPDATE inventory SET quantity = quantity + ?, last_updated = CURRENT_TIMESTAMP WHERE product_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, quantityChange);
            preparedStatement.setLong(2, productId);
            preparedStatement.executeUpdate();
            log.info("Stock updated for product ID: " + productId + " by " + quantityChange);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

