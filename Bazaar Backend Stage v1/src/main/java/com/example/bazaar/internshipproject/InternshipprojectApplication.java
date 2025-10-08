package com.example.bazaar.internshipproject;

import com.example.bazaar.internshipproject.config.DBConnect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.PreparedStatement;


@Slf4j
@SpringBootApplication
public class InternshipprojectApplication {

	public static void main(String[] args) {
		DBConnect dbConnect = new DBConnect();
		Connection connection = dbConnect.getConnection();
		if (connection != null) {
			log.info("Database connection established");
			//delete all entries of the inventory
//			try{
//				String query = "DROP TABLE stock_movements";
//				PreparedStatement preparedStatement = connection.prepareStatement(query);
//				int affectedRows = preparedStatement.executeUpdate();
//				if (affectedRows == 0) {
//					log.info("No rows affected");
//				} else {
//					log.info("Deleted {} rows from inventory", affectedRows);
//				}
//				String query2 = """
//						CREATE TABLE stock_movements (
//						    id INTEGER PRIMARY KEY AUTOINCREMENT,
//						    product_id INTEGER NOT NULL,
//						    change_type TEXT CHECK (change_type IN ('STOCK_IN', 'SALE', 'MANUAL_REMOVAL')),
//						    quantity INTEGER NOT NULL,
//						    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//						    FOREIGN KEY (product_id) REFERENCES inventory(product_id) ON DELETE CASCADE
//						);
//						""";
//				PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
//				int affectedRows2 = preparedStatement2.executeUpdate();
//				if (affectedRows2 == 0) {
//					log.info("No rows affected");
//				} else {
//					log.info("Deleted {} rows from inventory", affectedRows2);
//				}
//			}catch (Exception e){
//				log.error("Error deleting entries from inventory: {}", e.getMessage());
//			}
		} else {
			log.info("Database connection not established");
		}
		SpringApplication.run(InternshipprojectApplication.class, args);

	}

}
