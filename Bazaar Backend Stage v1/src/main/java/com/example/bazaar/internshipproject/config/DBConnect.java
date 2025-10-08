package com.example.bazaar.internshipproject.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Service
@Slf4j
public class DBConnect {

    private static Connection connection;
    public DBConnect() {
        connectDB();
    }

    public void connectDB(){
        String url= "jdbc:sqlite:storedb.db";
        try{
            connection = DriverManager.getConnection(url);
            log.info("Connected to database");

//            String createTableQuery = """
//           CREATE TABLE stock_movements (
//                    id INTEGER PRIMARY KEY AUTOINCREMENT,
//                    product_id INTEGER NOT NULL,
//                    change_type TEXT CHECK (change_type IN ('STOCK_IN', 'SALE', 'MANUAL_REMOVAL')) NOT NULL,
//                    quantity INTEGER NOT NULL,
//                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//                    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
//                );
//        """;

//            String createTableQuery2 = """
//           CREATE TABLE inventory (
//                  id INTEGER PRIMARY KEY AUTOINCREMENT,
//                  product_id INTEGER NOT NULL,
//                  quantity INTEGER NOT NULL DEFAULT 0,
//                  last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//                  FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
//              );
//        """;

//            try (Statement stmt = connection.createStatement()) {
//                stmt.execute(createTableQuery);
//            }

        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:storedb.db");
                connection.createStatement().execute("PRAGMA foreign_keys = ON;");
                connection.createStatement().execute("PRAGMA journal_mode=WAL;"); // ✅ Enable WAL mode
                connection.createStatement().execute("PRAGMA busy_timeout = 5000;"); // ✅ Prevent locking errors
            } catch (SQLException e) {
                throw new RuntimeException("Error connecting to SQLite database", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error closing SQLite connection", e);
        }
    }

}
