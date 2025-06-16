/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leanersdts;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
/**
 *
 * @author pitso
 */
public class PostgresDataContents {
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

                        // Execute a query to retrieve the contents of the table
                        try (Statement statement = conn.createStatement();
                             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName)) {

                            // Print the INSERT statements
                            ResultSetMetaData metaData = resultSet.getMetaData();
                            int columnCount = metaData.getColumnCount();

                            while (resultSet.next()) {
                                StringBuilder insertStatement = new StringBuilder("INSERT INTO " + tableName + " VALUES (");

                                for (int i = 1; i <= columnCount; i++) {
                                    String value = resultSet.getString(i);
                                    // Handle NULL values
                                    if (resultSet.wasNull()) {
                                        insertStatement.append("NULL");
                                    } else {
                                        // Escape single quotes in values
                                        value = value.replace("'", "''");
                                        insertStatement.append("'").append(value).append("'");
                                    }

                                    if (i < columnCount) {
                                        insertStatement.append(", ");
                                    }
                                }

                                insertStatement.append(");");

                                System.out.println(insertStatement.toString());
                            }

                            System.out.println();
                        }
                    }
                }
            }
        } catch (SQLException e) {
        }
    }
}
