package leanersdts;

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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class TakeQuizScreen implements ControlledScreen {

    @FXML
    private Text questionLabel;
    @FXML
    private Label timerLabel;
    private VBox optionsContainer;
    private Button nextButton;
    @FXML
    private Button previousButton;
    @FXML
    private Button skipButton;
    @FXML
    private Button reviewButton;

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

    public void initialize() {
        initializeShuffledQuestions();
        totalQuestions = shuffledQuestions.size();
        startQuizTimer();
        loadQuestion();
    }

    private void initializeShuffledQuestions() {
        QuizQuestionDatabase quizQuestionDatabase = new QuizQuestionDatabase();
        shuffledQuestions = quizQuestionDatabase.getRandomQuestions();
    }

    public void startQuizTimer() {
        quizStartTime = Instant.now();
        quizTimer = new Timeline(new KeyFrame(JavaFXDurationHandler.fromSeconds(1), event -> updateTimer()));
        quizTimer.setCycleCount(Timeline.INDEFINITE);
        quizTimer.play();
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }

   private void loadQuestion() {
    if (shuffledQuestions == null || shuffledQuestions.isEmpty()) {
    System.out.println("No questions available.");
    // Optionally disable UI components or display an error message
    return;
}

    // Get the current question based on the index
    QuizQuestion currentQuestion = shuffledQuestions.get(currentQuestionIndex);
    
    // Set the question text in the label
    questionLabel.setText(currentQuestion.getQuestionText());

    // Clear previous options
    optionsContainer.getChildren().clear();

    // Create a ToggleGroup to ensure only one option can be selected at a time
    ToggleGroup toggleGroup = new ToggleGroup();

    // Loop through the options of the current question
    for (int i = 0; i < currentQuestion.getOptions().length; i++) {
        // Create a RadioButton for each option
        RadioButton optionButton = new RadioButton(currentQuestion.getOptions()[i]);
        optionButton.setToggleGroup(toggleGroup);

        // Set an action handler to process the selected option
        final int optionIndex = i; // Need to make the index final for use in the lambda expression
        optionButton.setOnAction(event -> handleOptionSelected(optionIndex));

        // Add the RadioButton to the options container
        optionsContainer.getChildren().add(optionButton);
    }

    // Update navigation buttons based on the current question index
    updateNavigationButtons();
}


    private void updateNavigationButtons() {
        previousButton.setDisable(currentQuestionIndex == 0);
        nextButton.setDisable(currentQuestionIndex >= totalQuestions - 1);
    }

    private void handleOptionSelected(int selectedOptionIndex) {
        if (shuffledQuestions == null || shuffledQuestions.isEmpty()) {
    System.out.println("No questions available.");
    // Optionally disable UI components or display an error message
    return;
}


        QuizQuestion currentQuestion = shuffledQuestions.get(currentQuestionIndex);
        boolean isCorrect = (selectedOptionIndex == currentQuestion.getCorrectOption());

        java.time.Duration timeTaken = JavaTimeDurationHandler.between(quizStartTime, Instant.now()).minus(
                timeTakenPerQuestion.getOrDefault(currentQuestionIndex, java.time.Duration.ZERO));
        timeTakenPerQuestion.put(currentQuestionIndex, timeTaken);

        if (isCorrect) {
            correctAnswersCount++;
            answerStatusPerQuestion.put(currentQuestionIndex, "Correct");
        } else {
            String incorrectAnswerDetails = String.format("Question %d: %s\n", currentQuestionIndex + 1, JavaTimeDurationHandler.formatDuration(timeTaken));
            incorrectAnswerDetails += String.format("Answer Status: Incorrect\n");
            incorrectAnswerDetails += String.format("Question: %s\nCorrect Answer: %s\n\n",
                    currentQuestion.getQuestionText(),
                    currentQuestion.getOptions()[currentQuestion.getCorrectOption()]);
            answerStatusPerQuestion.put(currentQuestionIndex, "Incorrect");
            incorrectAnswersList.add(incorrectAnswerDetails);
        }

        incrementAndLoadOrShowSummary();
    }

    private void incrementAndLoadOrShowSummary() {
        currentQuestionIndex++;
        if (currentQuestionIndex < totalQuestions) {
            loadQuestion();
        } else {
            resetTimerAndShowQuizSummary();
        }
    }

    @FXML
    private void handleSkipButtonAction() {
        answerStatusPerQuestion.put(currentQuestionIndex, "Skipped");
        incrementAndLoadOrShowSummary();
    }

    @FXML
    private void handlePreviousButtonAction() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            loadQuestion();
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
        // Implementation to show a popup alert with the provided message
    }

    private void updateTimer() {
        java.time.Duration elapsedTime = JavaTimeDurationHandler.between(quizStartTime, Instant.now());
        timerLabel.setText(JavaTimeDurationHandler.formatDuration(elapsedTime));
    }

    @FXML
    private void handleOption1ButtonAction(ActionEvent event) {
        handleOptionSelected(0); // Assuming option 1 corresponds to index 0
    }

    @FXML
    private void handleOption2ButtonAction(ActionEvent event) {
        handleOptionSelected(1); // Assuming option 2 corresponds to index 1
    }

    @FXML
    private void handleOption3ButtonAction(ActionEvent event) {
        handleOptionSelected(2); // Assuming option 3 corresponds to index 2
    }

    @FXML
    private void handleOption4ButtonAction(ActionEvent event) {
        handleOptionSelected(3); // Assuming option 4 corresponds to index 3
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        // Implement navigation to the previous screen or previous question
    }

    @FXML
    private void handleReviewButtonAction(ActionEvent event) {
        // Implement navigation to the review screen or show review details
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
