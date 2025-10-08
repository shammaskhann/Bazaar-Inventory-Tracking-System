package com.example.bazaar.internshipproject.repositories;

import com.example.bazaar.internshipproject.config.DBConnect;
import com.example.bazaar.internshipproject.entities.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class ProductRepository  {

    public Product getProductById(long id,Connection connection) {
        // Fetch the newly inserted product
        try{
            String query2 = "SELECT * FROM products WHERE id = ?";
            PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
            preparedStatement2.setLong(1, id);
            ResultSet resultSet = preparedStatement2.executeQuery();
//            initializeInventory(id,connection);
            if (resultSet.next()) {
                Product insertedProduct = new Product();
                insertedProduct.setId(resultSet.getLong("id"));
                insertedProduct.setName(resultSet.getString("name"));
                insertedProduct.setDescription(resultSet.getString("description"));
                insertedProduct.setPrice(resultSet.getDouble("price"));
                insertedProduct.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                return insertedProduct;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Product not found");
    }



    public Product addProduct(Product product) {
        DBConnect connectDB = new DBConnect();
        Connection connection = connectDB.getConnection();

        if (connection != null) {
            log.info("Connection established");
            try {
                String query = "INSERT INTO products (name, description, price, created_at) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

                preparedStatement.setString(1, product.getName());
                preparedStatement.setString(2, product.getDescription());
                preparedStatement.setDouble(3, product.getPrice());
                preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating product failed, no rows affected.");
                }

                // Retrieve the generated ID
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    initializeInventory(generatedId,connection);
                    return getProductById(generatedId, connection);
                }

            } catch (SQLException e) {
                log.error("Error adding product", e);
                throw new RuntimeException(e);
            } finally {
                connectDB.closeConnection();
            }
        } else {
            log.error("Connection not established");
            throw new RuntimeException("Connection not established");
        }
        return null;
    }


    public List<Product> getAllProduct() {
       DBConnect connectDB = new DBConnect();
       Connection connection = connectDB.getConnection();
       if(connection != null){
           log.info("Connection established");
           String query = "SELECT * FROM products";
              try{
                  List<Product> products = new ArrayList<>();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next()){
                     Product product = new Product();
                     product.setId(resultSet.getLong("id"));
                     product.setName(resultSet.getString("name"));
                     product.setDescription(resultSet.getString("description"));
                     product.setPrice(resultSet.getDouble("price"));
                     product.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                        products.add(product);
                }
                return products;
              } catch (SQLException e) {
                throw new RuntimeException(e);
              } finally {
                connectDB.closeConnection();
              }
       }
       log.error("Connection not established");
       return null;
   }

    public boolean deleteProduct(Long id) {
        DBConnect connectDB = new DBConnect();
        Connection connection = connectDB.getConnection();

        if (connection != null) {
            log.info("Connection established for deletion");
            try {
                String query = "DELETE FROM products WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, id);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    log.info("Product deleted successfully");
                    return true; // ✅ Product was deleted
                } else {
                    log.warn("No product found with ID: " + id);
                    return false; // 🚨 Product not found
                }
            } catch (SQLException e) {
                log.error("Error deleting product", e);
                throw new RuntimeException(e);
            } finally {
                connectDB.closeConnection();
            }
        } else {
            log.error("Connection not established");
            throw new RuntimeException("Database connection not established");
        }
    }

    private void initializeInventory(Long productId,Connection connection) {
        String query = "INSERT INTO inventory (product_id, quantity) VALUES (?, 0)";
        try
             {
                 PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, productId);
            statement.executeUpdate();
            log.info("Inventory initialized for product ID: " + productId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
