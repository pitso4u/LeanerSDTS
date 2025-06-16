/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leanersdts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseConnector {
    private static final Logger logger = Logger.getLogger(DatabaseConnector.class.getName());
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/leanersdts";
    private static final String USER = "postgres";
    private static final String PASS = "postgres";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            logger.info("Attempting to connect to database: " + DB_URL);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            logger.info("Database connection established successfully");
            return conn;
        } catch (ClassNotFoundException e) {
            logger.severe("PostgreSQL JDBC Driver not found");
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        } catch (SQLException e) {
            logger.severe("Failed to connect to database");
            throw e;
        }
    }
}

