package main.java.leanersdts;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.control.Dialog;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class TakeQuizScreen implements ControlledScreen {
    @FXML
    private Label timerLabel;
    @FXML
    private Text questionLabel;
    private VBox optionsContainer; // Ensure this is properly annotated
    @FXML
    private Button previousButton;
    @FXML
    private Button skipButton;
    @FXML
    private Button reviewButton;
    @FXML
    private Button option1Button;
    @FXML
    private Button option2Button;
    @FXML
    private Button option3Button;
    @FXML
    private Button option4Button;
    @FXML
    private ImageView questionImageView;

    private ScreenManager screenManager;
    private List<QuizQuestion> shuffledQuestions;
    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;
    private int totalQuestions;
    private Timeline quizTimer;
    private Instant quizStartTime;
    private Map<Integer, String> answerStatusPerQuestion = new HashMap<>();
    private List<String> incorrectAnswersList = new ArrayList<>();
    private Map<Integer, java.time.Duration> timeTakenPerQuestion = new HashMap<>();
    private LoginData loginData;
    private List<Integer> selectedAnswers = new ArrayList<>();

    public void initialize() {
        System.out.println("Initializing TakeQuizScreen"); // Debug print
        QuizQuestionDatabase database = new QuizQuestionDatabase();
        shuffledQuestions = database.getRandomQuestions();
        
        if (shuffledQuestions.isEmpty()) {
            showErrorAlert("Error", "No Questions Available");
            return;
        }
        
        // Initialize selectedAnswers with -1 for each question
        for (int i = 0; i < shuffledQuestions.size(); i++) {
            selectedAnswers.add(-1);
        }
        
        currentQuestionIndex = 0;
        initializeUI();
        startQuizTimer();
        loadQuestion(currentQuestionIndex);
    }

    private void initializeUI() {
        // Initialize UI components
        previousButton.setDisable(true);
        skipButton.setDisable(false);
        reviewButton.setDisable(false);
        timerLabel.setText("Time Remaining: 60:00");
    }

    private void initializeShuffledQuestions() {
        QuizQuestionDatabase quizQuestionDatabase = new QuizQuestionDatabase();
        shuffledQuestions = quizQuestionDatabase.getRandomQuestions();
    }

    public void startQuizTimer() {
        quizStartTime = Instant.now();
        quizTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimer()));
        quizTimer.setCycleCount(Timeline.INDEFINITE);
        quizTimer.play();
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }
    
    private void loadQuestion(int index) {
        if (index < 0 || index >= shuffledQuestions.size()) {
            showErrorAlert("Error", "Invalid question index.");
            return;
        }

        QuizQuestion question = shuffledQuestions.get(index);
        questionLabel.setText(question.getQuestionText());
        optionsContainer.getChildren().clear();

        ToggleGroup group = new ToggleGroup();
        String[] options = question.getOptions();
        for (int i = 0; i < options.length; i++) {
            RadioButton optionButton = new RadioButton(options[i]);
            optionButton.setToggleGroup(group);
            int optionIndex = i;
            optionButton.setOnAction(event -> {
                try {
                    handleOptionSelected(optionIndex);
                } catch (IOException ex) {
                    Logger.getLogger(TakeQuizScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            optionsContainer.getChildren().add(optionButton);
        }
    }

    private void handleOptionSelected(int selectedOptionIndex) throws IOException {
        if (shuffledQuestions == null || shuffledQuestions.isEmpty()) {
            showErrorAlert("Error", "No questions available.");
            return;
        }

        QuizQuestion currentQuestion = shuffledQuestions.get(currentQuestionIndex);
        currentQuestion.setUserAnswerIndex(selectedOptionIndex);
        selectedAnswers.set(currentQuestionIndex, selectedOptionIndex);

        // Proceed to the next question or submit the quiz
        if (currentQuestionIndex < shuffledQuestions.size() - 1) {
            currentQuestionIndex++;
            loadQuestion(currentQuestionIndex);
        } else {
            submitQuiz();
        }
    }

    private void updateNavigationButtons() {
        previousButton.setDisable(currentQuestionIndex == 0);
        skipButton.setDisable(currentQuestionIndex == shuffledQuestions.size() - 1);
        reviewButton.setDisable(false); // Enable review always for now
    }

    private void submitQuiz() throws IOException {
        quizTimer.stop();

        // Load the screen and get the controller directly
        ReviewScreenController reviewController = (ReviewScreenController) screenManager.loadScreen(LeanerSDTS.ReviewScreenID, LeanerSDTS.ReviewScreenFile);

        if (reviewController != null) {
            reviewController.setData(shuffledQuestions, new ArrayList<>(selectedAnswers));
            screenManager.setScreen(LeanerSDTS.ReviewScreenID);
        } else {
            showErrorAlert("Error", "Could not load review screen");
        }
    }
    @FXML
    private void handleSkipButtonAction() throws IOException {
        answerStatusPerQuestion.put(currentQuestionIndex, "Skipped");
        
        // Directly handle the logic here
        if (currentQuestionIndex < shuffledQuestions.size() - 1) {
            currentQuestionIndex++;
            loadQuestion(currentQuestionIndex);
        } else {
            submitQuiz(); // Show summary if it's the last question
        }
    }

    @FXML
    private void handlePreviousButtonAction() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            loadQuestion(currentQuestionIndex);
        }
    }

    private void resetTimerAndShowQuizSummary() {
        quizTimer.stop();
        showQuizSummary();
    }

    private void showQuizSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Quiz Completed!\n");
        summary.append("Correct Answers: ").append(correctAnswersCount).append(" / ").append(totalQuestions).append("\n");
        summary.append("Time Taken: ").append(JavaTimeDurationHandler.formatDuration(JavaTimeDurationHandler.between(quizStartTime, Instant.now()))).append("\n");

        if (!incorrectAnswersList.isEmpty()) {
            summary.append("\nIncorrect Answers Review:\n");
            for (String incorrectAnswer : incorrectAnswersList) {
                summary.append(incorrectAnswer);
            }
        }

        showAlert(summary.toString());
    }

    private void showAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Quiz Summary");
    alert.setContentText(message);
    alert.showAndWait();
}

private void reviewAnswers() {
    // Create a dialog to show the review of answers
    Dialog<Void> reviewDialog = new Dialog<>();
    reviewDialog.setTitle("Review Your Answers");

    VBox reviewContent = new VBox(10);
    reviewContent.setPadding(new Insets(10));

    for (int i = 0; i < shuffledQuestions.size(); i++) {
        QuizQuestion question = shuffledQuestions.get(i);
        String answerText = "No answer selected"; // Default text

        // Check the type of question and retrieve the selected answer
        if (question instanceof SignQuestion) {
            SignQuestion signQuestion = (SignQuestion) question;
            if (signQuestion.getUserAnswerIndex() != -1) {
                answerText = "Your answer: " + signQuestion.getOptions()[signQuestion.getUserAnswerIndex()];
            }
        } else if (question instanceof RuleQuestion) {
            RuleQuestion ruleQuestion = (RuleQuestion) question;
            if (ruleQuestion.getUserAnswerIndex() != -1) {
                answerText = "Your answer: " + ruleQuestion.getOptions()[ruleQuestion.getUserAnswerIndex()];
            }
        } else if (question instanceof ControlQuestion) {
            ControlQuestion controlQuestion = (ControlQuestion) question;
            if (controlQuestion.getUserAnswerIndex() != -1) {
                answerText = "Your answer: " + controlQuestion.getOptions()[controlQuestion.getUserAnswerIndex()];
            }
        }

        Label questionLabel = new Label((i + 1) + ". " + question.getQuestionText() + "\n" + answerText);
        reviewContent.getChildren().add(questionLabel);
    }

    reviewDialog.getDialogPane().setContent(reviewContent);

    ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
    reviewDialog.getDialogPane().getButtonTypes().add(closeButton);

    reviewDialog.showAndWait();
}

    private void updateTimer() {
        java.time.Duration elapsedTime = JavaTimeDurationHandler.between(quizStartTime, Instant.now());
        timerLabel.setText(JavaTimeDurationHandler.formatDuration(elapsedTime));
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        screenManager.setScreen(LeanerSDTS.DashboardScreenID);
    }

    @FXML
    private void handleReviewButtonAction(ActionEvent event) {
        reviewAnswers();
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

    private void handleNextButtonAction() {
        if (currentQuestionIndex < shuffledQuestions.size() - 1) {
            currentQuestionIndex++;
            loadQuestion(currentQuestionIndex);
        }
    }

    private void handleSubmitButtonAction() throws IOException {
        submitQuiz();
    }

    private void showQuizResults(double score, int totalCorrect) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Results");
        alert.setHeaderText("Quiz Complete!");
        alert.setContentText(String.format("You scored %.2f%%\nCorrect answers: %d/%d", 
            score, totalCorrect, shuffledQuestions.size()));
        alert.showAndWait();
        
        screenManager.setScreen(LeanerSDTS.QuizSummaryScreenID);
    }

    private void showTimeUpAlert() throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Time Up");
        alert.setHeaderText("Quiz Time Has Expired");
        alert.setContentText("Your answers will be submitted automatically.");
        alert.showAndWait();
        
        submitQuiz();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleOption1ButtonAction(ActionEvent event) {
    }

    @FXML
    private void handleOption2ButtonAction(ActionEvent event) {
    }

    @FXML
    private void handleOption3ButtonAction(ActionEvent event) {
    }

    @FXML
    private void handleOption4ButtonAction(ActionEvent event) {
    }
}