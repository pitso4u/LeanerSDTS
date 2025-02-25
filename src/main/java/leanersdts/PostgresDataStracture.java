/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.leanersdts;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public class PostgresDataStracture {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5433/smartdrive_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "Soetsang@144156"; // Replace with your actual password

    public static void main(String[] args) {
        try {
            // Connect to the database
            try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {

                // Get metadata about the database
                DatabaseMetaData meta = conn.getMetaData();

                // Get a list of all the tables in the database
                try (ResultSet tables = meta.getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (tables.next()) {
                        // Get the name of the table
                        String tableName = tables.getString("TABLE_NAME");

                        // Get the list of fields in the table
                        try (ResultSet fields = meta.getColumns(null, null, tableName, "%")) {

                            // Build the "CREATE TABLE" statement for the table
                            StringBuilder createTableSQL = new StringBuilder("CREATE TABLE " + tableName + "(");
                            while (fields.next()) {
                                // Get the name and data type of the field
                                String fieldName = fields.getString("COLUMN_NAME");
                                String fieldType = fields.getString("DATA_TYPE");

                                // Append the field to the "CREATE TABLE" statement
                                createTableSQL.append(fieldName).append(" ").append(fieldType).append(", ");
                            }

                            // Remove the final comma and close the parentheses
                            createTableSQL.setLength(createTableSQL.length() - 2);
                            createTableSQL.append(")");

                            // Print the "CREATE TABLE" statement
                            System.out.println(createTableSQL.toString());
                        }
                    }
                }
            }
        } catch (SQLException e) {
        }
    }
}
