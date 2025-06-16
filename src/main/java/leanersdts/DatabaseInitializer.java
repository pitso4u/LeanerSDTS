package leanersdts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/smartdrive_db";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "Soetsang@144156";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            // Create tables
            createTables(conn);
            
            // Insert sample questions
            insertSampleQuestions(conn);
            
            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Create signs table
            stmt.execute("CREATE TABLE IF NOT EXISTS signs (" +
                    "question_id SERIAL PRIMARY KEY, " +
                    "question_text TEXT NOT NULL, " +
                    "image_url TEXT, " +
                    "has_image BOOLEAN DEFAULT false, " +
                    "option1 TEXT NOT NULL, " +
                    "option2 TEXT NOT NULL, " +
                    "option3 TEXT NOT NULL, " +
                    "option4 TEXT NOT NULL, " +
                    "correct_option INTEGER NOT NULL)");

            // Create controls table
            stmt.execute("CREATE TABLE IF NOT EXISTS controls (" +
                    "question_id SERIAL PRIMARY KEY, " +
                    "question_text TEXT NOT NULL, " +
                    "image_url TEXT, " +
                    "has_image BOOLEAN DEFAULT false, " +
                    "option1 TEXT NOT NULL, " +
                    "option2 TEXT NOT NULL, " +
                    "option3 TEXT NOT NULL, " +
                    "option4 TEXT NOT NULL, " +
                    "correct_option INTEGER NOT NULL)");

            // Create rules table
            stmt.execute("CREATE TABLE IF NOT EXISTS rules (" +
                    "question_id SERIAL PRIMARY KEY, " +
                    "question_text TEXT NOT NULL, " +
                    "image_url TEXT, " +
                    "has_image BOOLEAN DEFAULT false, " +
                    "option1 TEXT NOT NULL, " +
                    "option2 TEXT NOT NULL, " +
                    "option3 TEXT NOT NULL, " +
                    "option4 TEXT NOT NULL, " +
                    "correct_option INTEGER NOT NULL)");
        }
    }

    private static void insertSampleQuestions(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Insert sample sign questions
            stmt.execute("INSERT INTO signs (question_text, option1, option2, option3, option4, correct_option) VALUES " +
                    "('What does a red traffic light mean?', 'Stop', 'Go', 'Proceed with caution', 'Speed up', 1), " +
                    "('What does a yellow traffic light mean?', 'Stop', 'Go', 'Proceed with caution', 'Speed up', 3), " +
                    "('What does a green traffic light mean?', 'Stop', 'Go', 'Proceed with caution', 'Speed up', 2)");

            // Insert sample control questions
            stmt.execute("INSERT INTO controls (question_text, option1, option2, option3, option4, correct_option) VALUES " +
                    "('What is the purpose of the steering wheel?', 'To control direction', 'To control speed', 'To control braking', 'To control lights', 1), " +
                    "('What is the purpose of the brake pedal?', 'To control direction', 'To control speed', 'To control braking', 'To control lights', 3), " +
                    "('What is the purpose of the accelerator?', 'To control direction', 'To control speed', 'To control braking', 'To control lights', 2)");

            // Insert sample rule questions
            stmt.execute("INSERT INTO rules (question_text, option1, option2, option3, option4, correct_option) VALUES " +
                    "('What is the speed limit in a residential area?', '30 km/h', '50 km/h', '60 km/h', '80 km/h', 2), " +
                    "('What is the speed limit on a highway?', '80 km/h', '100 km/h', '120 km/h', '140 km/h', 3), " +
                    "('What is the speed limit in a school zone?', '20 km/h', '30 km/h', '40 km/h', '50 km/h', 2)");
        }
    }
} 