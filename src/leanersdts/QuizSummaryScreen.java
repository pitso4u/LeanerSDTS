/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leanersdts;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class QuizSummaryScreen implements ControlledScreen {

    @FXML
    private Label learnerLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label timeTakenLabel;

    @FXML
    private TextArea summaryTextArea;

    @FXML
    private Label passFailLabel;

    @FXML
    private Label resultLabel;

    private Label summaryTextLabel;

    private ScreenManager screenManager;
    private Map<Integer, Duration> timeTakenPerQuestion;
    private Map<Integer, String> answerStatusPerQuestion;
    private List<String> incorrectAnswersList;
    private String learnerName;
    private LoginData loginDat;
    private int correctAnswersCount;
    private int totalQuestions;
    @FXML
    private SanFranciscoFireworks fireworks = new SanFranciscoFireworks();
    @FXML
    private ImageView carImageView;

    public void initialize() {
        loadCarImage();
    }

    private void loadCarImage() {
        try {

            String svgPathData = "M20 20 L180 20 L180 80 L20 80 Z M30 10 L170 10 L170 30 L30 30 Z M45 30 L75 30 L75 60 L45 60 Z M125 30 L155 30 L155 60 L125 60 Z M50 80 A10 10 0 1 0 50 60 A10 10 0 1 0 50 80 Z M150 80 A10 10 0 1 0 150 60 A10 10 0 1 0 150 80 Z";

            SVGPath svgPath = new SVGPath();
            svgPath.setContent(svgPathData);
            // Create an Image from the SVGPath
            Image carImage = createImageFromSVG(svgPath);

            // Set the Image to the ImageView
            carImageView.setImage(carImage);

            // Apply any animation or styling as needed
            //applyCarAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Image createImageFromSVG(SVGPath svgPath) {
        int imageSize = 100;

        ImageView imageView = new ImageView();
        imageView.setClip(svgPath);
        imageView.setFitWidth(imageSize);
        imageView.setFitHeight(imageSize);

        return imageView.snapshot(null, null);
    }

    private void applyCarAnimation() {
        // Apply animation to the ImageView (CSS or JavaFX Animation API)
        // Example using CSS animation (adjust the animation properties):
        carImageView.getStyleClass().add("car-animation");
    }

    public void setLoginDat(LoginData loginDat) {
        this.loginDat = loginDat;
    }

    public void setQuizDetails(Map<Integer, Duration> timeTakenPerQuestion, Map<Integer, String> answerStatusPerQuestion, List<String> incorrectAnswersList) {
        this.timeTakenPerQuestion = timeTakenPerQuestion;
        this.answerStatusPerQuestion = answerStatusPerQuestion;
        this.incorrectAnswersList = incorrectAnswersList;
        displaySummary();

        // Check if the learner has passed (you need to implement this logic)
        boolean passed = checkIfPassed(); // Implement this method based on your passing criteria

        // If the learner has passed, play the fireworks celebration
        if (passed) {
            fireworks.start();
        }
    }
    // Method to check if the learner has passed (you need to implement this logic)

    private boolean checkIfPassed() {
        // Implement your passing criteria and return true if passed, false otherwise
        // For example, you can check the percentage of correct answers
        double percentage = (double) correctAnswersCount / totalQuestions * 100;
        return percentage >= 80.0;
    }

    public void setLearnerName(LoginData loginDat) {
        this.loginDat = loginDat;
        learnerLabel.setText("Learner: " + loginDat.getFull_name());
    }

    public void setLearnerName(String learnerName) {
        this.learnerName = learnerName;
    }

    public void setLearnerLoginDat(LoginData loginDat) {
        this.loginDat = loginDat;
    }

    public void setIncorrectAnswersList(Map<Integer, String> incorrectAnswersList) {
        this.answerStatusPerQuestion = incorrectAnswersList;
    }

    private void showQuizSummary() {
        screenManager.setScreen("QuizSummaryScreen");
        QuizSummaryScreen quizSummaryScreen = (QuizSummaryScreen) screenManager.getController("QuizSummaryScreen");

        // Check if correctAnswersCount and totalQuestions are properly set
        if (correctAnswersCount >= 0 && totalQuestions > 0) {
            double percentage = (double) correctAnswersCount / totalQuestions * 100;
            String summary = String.format("%.2f%% (%d out of %d)", percentage, correctAnswersCount, totalQuestions);

            quizSummaryScreen.setDate(LocalDateTime.now().toString());
            quizSummaryScreen.setTimeTaken(formatDuration(timeTakenPerQuestion.get(totalQuestions - 1)));
            quizSummaryScreen.setSummaryText(summary);
            quizSummaryScreen.setPassFailLabel(percentage);
            quizSummaryScreen.setQuizDetails(timeTakenPerQuestion, answerStatusPerQuestion, incorrectAnswersList);
        } else {
            // Handle the case where correctAnswersCount and totalQuestions are not set
            System.err.println("Error: correctAnswersCount or totalQuestions not set.");
        }
    }

    // Method to set various learner details upon login
    public void setLearnerDetails(LoginData loginDat, Map<Integer, Duration> timeTakenPerQuestion, Map<Integer, String> answerStatusPerQuestion, List<String> incorrectAnswersList) {
        this.loginDat = loginDat;
        this.timeTakenPerQuestion = timeTakenPerQuestion;
        this.answerStatusPerQuestion = answerStatusPerQuestion;
        this.incorrectAnswersList = incorrectAnswersList;

        // Display learner's full name
        learnerLabel.setText("Learner: " + loginDat.getFull_name());

        // Show quiz summary based on the provided data
        showQuizSummary();
    }

    private String formatDuration(Duration duration) {
        long minutes = duration.toMinutes();
        long seconds = duration.minusMinutes(minutes).getSeconds();
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void setQuizDetails(LoginData loginDat, String date, String timeTaken, String result, String summaryText) {
        learnerLabel.setText("Learner: " + loginDat.getFull_name());
        dateLabel.setText("Date: " + date);
        timeTakenLabel.setText("Time Taken: " + timeTaken);
        resultLabel.setText("Result: " + summaryText);
        summaryTextLabel.setText("Summary: " + summaryText);
        displaySummary();
    }

    public void setPassFailLabel(double percentage) {
        String result = percentage >= 80.0 ? "Pass" : "Fail, Sorry try again";
        passFailLabel.setText(result);
        passFailLabel.setTextFill(percentage >= 80.0 ? Color.GREEN : Color.RED); // Adjust colors as needed
    }

    public void startFireworks() {
        fireworks.start();
    }

    public void stopFireworks() {
        fireworks.stop();
    }

    @FXML
    private void handleBackButtonAction() {
        // Stop the fireworks when navigating back
        stopFireworks();
        screenManager.setScreen("DashboardScreen");
    }

    private void displaySummary() {
        StringBuilder summary = new StringBuilder("Quiz Summary:\n\n");

        for (int i = 0; i < timeTakenPerQuestion.size(); i++) {
            Duration timeTaken = timeTakenPerQuestion.getOrDefault(i, Duration.ZERO);
            String answerStatus = answerStatusPerQuestion.getOrDefault(i, "");

            summary.append(String.format("Question %d: %s\n", i + 1, formatDuration(timeTaken)));
            summary.append(String.format("Answer Status: %s\n", answerStatus.equals("Correct") ? "Correct" : "Incorrect"));

            if (answerStatus.equals("Incorrect") && incorrectAnswersList != null && i < incorrectAnswersList.size()) {
                // Add details for incorrect answers directly from incorrectAnswersList
                String incorrectAnswerDetails = incorrectAnswersList.get(i);
                summary.append(String.format("Question: %s\n", incorrectAnswerDetails));
                // You can add more details like Correct Answer, etc., based on your data structure
            } else {
                // Handle the case where incorrectAnswersList is null or index is out of bounds
                summary.append("Details not available\n");
            }

            summary.append("\n");
        }

        summaryTextArea.appendText(summary.toString());
    }

    public void setDate(String date) {
        dateLabel.setText(date);
    }

    public void setTimeTaken(String timeTaken) {
        timeTakenLabel.setText(timeTaken);
    }

    public void setSummaryText(String summaryText) {
        resultLabel.setText(summaryText);
    }

    public void setPassFailLabel(boolean passed) {
        passFailLabel.setText(passed ? "Pass" : "Fail, Sorry try again");
        passFailLabel.setTextFill(passed ? Color.GREEN : Color.RED); // Adjust colors as needed
    }

    @Override
    public void setScreenParent(ScreenManager screenParent) {
        this.screenManager = screenParent;
    }

    @Override
    public void runOnScreenChange() {
        // Code to run when the screen is changed, if needed
    }

    @Override
    public void cleanup() {
        // Cleanup code, if needed
    }
}
