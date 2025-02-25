package main.java.leanersdts;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import javafx.event.ActionEvent;

public class ReviewScreenController implements ControlledScreen {

    @FXML
    private VBox reviewContainer;
private ScreenManager screenManager;
    private List<QuizQuestion> shuffledQuestions;
    private List<Integer> selectedAnswers;

    public void setData(List<QuizQuestion> questions, List<Integer> answers) {
        this.shuffledQuestions = questions;
        this.selectedAnswers = answers;
        loadReviewContent();
    }

    private void loadReviewContent() {
        reviewContainer.getChildren().clear();
        for (int i = 0; i < shuffledQuestions.size(); i++) {
            QuizQuestion question = shuffledQuestions.get(i);
            Label questionLabel = new Label((i + 1) + ". " + question.getQuestionText());
            reviewContainer.getChildren().add(questionLabel);

            // Display selected answer
            int selectedAnswer = selectedAnswers.get(i);
            Label answerLabel = new Label("Your answer: " + question.getOptions()[selectedAnswer]);
            reviewContainer.getChildren().add(answerLabel);

            // Create a ComboBox for changing the answer
            ComboBox<String> answerComboBox = new ComboBox<>();
            for (String option : question.getOptions()) {
                answerComboBox.getItems().add(option);
            }
            answerComboBox.setValue(question.getOptions()[selectedAnswer]); // Set current answer

            // Use a final variable to hold the index
            final int index = i; // Effectively final variable
            answerComboBox.setOnAction(event -> {
                // Update the selected answer when changed
                selectedAnswers.set(index, answerComboBox.getSelectionModel().getSelectedIndex());
            });
            reviewContainer.getChildren().add(answerComboBox);
        }
    }

    @FXML
    private void handleSubmitButtonAction() {
        // Logic to submit answers and go to results screen
        // You can call a method to calculate results based on selectedAnswers
    }

    private void handleBackToDashboardButtonAction() {
        screenManager.setScreen(LeanerSDTS.DashboardScreenID);
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

    @FXML
    private void handleBackToQuizButtonAction(ActionEvent event) {
    }
}
