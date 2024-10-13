package leanersdts;

// Method to retrieve random questions from the QuizQuestionDatabase
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizQuestionDatabase {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/smartdrive_db"; // Update this to your database path

    private static final int MIN_RULES = 22;
    private static final int MIN_SIGNS = 23;
    private static final int MIN_CONTROLS = 6;

    public List<QuizQuestion> getRandomQuestions() {
        List<QuizQuestion> questions = new ArrayList<>();

        // Fetch minimum required questions from each category
        questions.addAll(fetchQuestions("rules", MIN_RULES));
        questions.addAll(fetchQuestions("signs", MIN_SIGNS));
        questions.addAll(fetchQuestions("controls", MIN_CONTROLS));

        // Shuffle and return all questions
        Collections.shuffle(questions);
        return questions;
    }

    private List<QuizQuestion> fetchQuestions(String tableName, int minCount) {
        List<QuizQuestion> questions = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " ORDER BY RANDOM() LIMIT " + minCount;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                QuizQuestion question = createQuizQuestion(
                        tableName,
                        rs.getInt("question_id"),
                        rs.getString("question_text"),
                        new String[]{rs.getString("option1"), rs.getString("option2"), rs.getString("option3"), rs.getString("option4")},
                        rs.getInt("correct_option"),
                        rs.getString("image_url"),
                        rs.getBoolean("has_image")
                );
                questions.add(question);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return questions;
    }

    private QuizQuestion createQuizQuestion(String tableName, int questionId, String questionText, String[] options, int correctOption, String imageUrl, boolean hasImage) {
        switch (tableName) {
            case "rules":
                return new RuleQuestion(questionText, options, correctOption, imageUrl, hasImage);
            case "signs":
                return new SignQuestion(questionText, options, correctOption, imageUrl, hasImage);
            case "controls":
                return new ControlQuestion(questionText, options, correctOption, imageUrl, hasImage);
            default:
                throw new IllegalArgumentException("Unknown table: " + tableName);
        }
    }
}

