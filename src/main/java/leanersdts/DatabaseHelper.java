package main.java.leanersdts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static DatabaseHelper instance;
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5433/smartdrive_db";
private static final String USERNAME = "postgres";
    private static final String PASSWORD = "Soetsang@144156";
    // Table names
    private static final String TABLE_CONTROLS = "controls";
    private static final String TABLE_INSTRUCTORS = "instructors";
    private static final String TABLE_LEARNERS = "learners";
    private static final String TABLE_PROGRESS_TRACKING = "progress_tracking";
    private static final String TABLE_RULES = "rules";
    private static final String TABLE_SCHEDULE = "schedule";
    private static final String TABLE_SIGNS = "signs";
    private static final String TABLE_PACKAGES = "packages";

    // Column names
    private static final String COLUMN_PACKAGE_NAME = "package_name";

    // Private constructor to enforce singleton pattern
    private DatabaseHelper() {
        // Initialize the database connection and create tables if not existing
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_CONTROLS_TABLE);
            stmt.execute(CREATE_INSTRUCTORS_TABLE);
            stmt.execute(CREATE_LEARNERS_TABLE);
            stmt.execute(CREATE_PROGRESS_TRACKING_TABLE);
            stmt.execute(CREATE_RULES_TABLE);
            stmt.execute(CREATE_SCHEDULE_TABLE);
            stmt.execute(CREATE_SIGNS_TABLE);
            stmt.execute(CREATE_PACKAGES_TABLE);
            stmt.execute(CREATE_LEARNER_PACKAGES_TABLE);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Singleton instance access
    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    // Connect to the database
    private Connection connect() {
        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Create tables SQL
    private static final String CREATE_CONTROLS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CONTROLS + " (" +
                    "question_id INTEGER PRIMARY KEY, " +
                    "question_text TEXT, " +
                    "image_url TEXT, " +
                    "has_image INTEGER, " +
                    "option1 TEXT, " +
                    "option2 TEXT, " +
                    "option3 TEXT, " +
                    "option4 TEXT, " +
                    "correct_option INTEGER);";

    private static final String CREATE_INSTRUCTORS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_INSTRUCTORS + " (" +
                    "instructor_id INTEGER PRIMARY KEY, " +
                    "instructor_name TEXT, " +
                    "contact_number TEXT, " +
                    "email TEXT, " +
                    "hire_date DATE, " +
                    "specialization TEXT);";

    private static final String CREATE_LEARNERS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_LEARNERS + " (" +
                    "learner_id INTEGER PRIMARY KEY," +
                    "username TEXT," +
                    "password TEXT," +
                    "full_name TEXT," +
                    "date_of_birth TEXT," +
                    "email TEXT," +
                    "address TEXT," +
                    "contact_number TEXT);";

    private static final String CREATE_PROGRESS_TRACKING_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PROGRESS_TRACKING + " (" +
                    "progress_id INTEGER PRIMARY KEY, " +
                    "learner_id INTEGER, " +
                    "module_name TEXT, " +
                    "quiz_score INTEGER, " +
                    "completion_status INTEGER, " +
                    "timestamp DATETIME, " +
                    "instructor_id INTEGER, " +
                    "session_date DATE, " +
                    "session_start_time TIME, " +
                    "session_end_time TIME, " +
                    "notes TEXT, " +
                    "correct_control INTEGER, " +
                    "total_control INTEGER, " +
                    "correct_rule INTEGER, " +
                    "total_rule INTEGER, " +
                    "correct_sign INTEGER, " +
                    "total_sign INTEGER);";

    private static final String CREATE_RULES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_RULES + " (" +
                    "question_id INTEGER PRIMARY KEY, " +
                    "question_text TEXT, " +
                    "image_url TEXT, " +
                    "has_image INTEGER, " +
                    "option1 TEXT, " +
                    "option2 TEXT, " +
                    "option3 TEXT, " +
                    "option4 TEXT, " +
                    "correct_option INTEGER);";

    private static final String CREATE_SCHEDULE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SCHEDULE + " (" +
                    "schedule_id INTEGER PRIMARY KEY, " +
                    "learner_id INTEGER, " +
                    "session_date DATE, " +
                    "session_start_time TIME, " +
                    "session_end_time TIME);";

    private static final String CREATE_SIGNS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SIGNS + " (" +
                    "question_id INTEGER PRIMARY KEY, " +
                    "question_text TEXT, " +
                    "image_url TEXT, " +
                    "has_image INTEGER, " +
                    "option1 TEXT, " +
                    "option2 TEXT, " +
                    "option3 TEXT, " +
                    "option4 TEXT, " +
                    "correct_option INTEGER);";

    private static final String CREATE_PACKAGES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PACKAGES + " (" +
                    "package_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "package_name TEXT, " +
                    "package_description TEXT, " +
                    "package_price REAL);";

    private static final String CREATE_LEARNER_PACKAGES_TABLE =
            "CREATE TABLE IF NOT EXISTS learner_packages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "learner_id INTEGER, " +
                    "package_id INTEGER, " +
                    "tests_remaining INTEGER, " +
                    "start_date TEXT, " +
                    "FOREIGN KEY(learner_id) REFERENCES learners(id));";

    // Method to fetch package names
    public List<String> getPackages() {
        List<String> packages = new ArrayList<>();
        String sql = "SELECT " + COLUMN_PACKAGE_NAME + " FROM " + TABLE_PACKAGES;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                packages.add(rs.getString(COLUMN_PACKAGE_NAME));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return packages;
    }
}

//public class DatabaseConnector {
//    private static final String DB_URL = "jdbc:postgresql://localhost:5433/smartdrive_db";
//    private static final String USERNAME = System.getenv("DB_USERNAME");
//    private static final String PASSWORD = System.getenv("DB_PASSWORD");
//
//    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
//    }
//}
