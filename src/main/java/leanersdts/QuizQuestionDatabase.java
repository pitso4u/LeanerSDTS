package main.java.leanersdts;

// Method to retrieve random questions from the QuizQuestionDatabase
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizQuestionDatabase {
    private static final String DB_URL = "jdbc:postgresql://localhost:5433/smartdrive_db"; // Update this to your database path
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "Soetsang@144156"; // Make sure this matches your DB password

    private static final int SIGN_QUESTION_LIMIT = 28;
    private static final int CONTROL_QUESTION_LIMIT = 7;
    private static final int RULE_QUESTION_LIMIT = 28;

    public List<QuizQuestion> getRandomQuestions() {
        List<QuizQuestion> questions = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            // Fetch questions from each category with new limits
            questions.addAll(fetchQuestions(conn, "signs", SIGN_QUESTION_LIMIT));
            questions.addAll(fetchQuestions(conn, "controls", CONTROL_QUESTION_LIMIT));
            questions.addAll(fetchQuestions(conn, "rules", RULE_QUESTION_LIMIT));

            if (questions.isEmpty()) {
                System.err.println("Warning: No questions were loaded from the database!");
            }

        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }

        // Shuffle all questions
        Collections.shuffle(questions);
        return questions;
    }

    private List<QuizQuestion> fetchQuestions(Connection conn, String tableName, int limit) {
        List<QuizQuestion> questions = new ArrayList<>();
        String query = "SELECT * FROM " + tableName + " ORDER BY RANDOM() LIMIT ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    QuizQuestion question = createQuestionFromResultSet(rs, tableName);
                    if (question != null) {
                        questions.add(question);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching questions from " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return questions;
    }

    private QuizQuestion createQuestionFromResultSet(ResultSet rs, String tableName) throws SQLException {
        String questionText = rs.getString("question_text");
        String[] options = new String[4];
        options[0] = rs.getString("option1");
        options[1] = rs.getString("option2");
        options[2] = rs.getString("option3");
        options[3] = rs.getString("option4");
        int correctOption = rs.getInt("correct_option");
        String imageUrl = rs.getString("image_url");
        boolean hasImage = rs.getBoolean("has_image");

        switch (tableName) {
            case "signs":
                return new SignQuestion(questionText, options, correctOption, imageUrl, hasImage);
            case "controls":
                return new ControlQuestion(questionText, options, correctOption, imageUrl, hasImage);
            case "rules":
                return new RuleQuestion(questionText, options, correctOption, imageUrl, hasImage);
            default:
                return null;
        }
    }
}

