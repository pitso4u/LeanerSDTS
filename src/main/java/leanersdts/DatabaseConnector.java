/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.leanersdts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

   private static final String JDBC_URL = "jdbc:postgresql://localhost:5433/smartdrive_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "Soetsang@144156";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}

