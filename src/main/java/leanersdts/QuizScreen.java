package leanersdts;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.*;

public class QuizScreen {
    @FXML private Label timerLabel;
    @FXML private Label questionNumberLabel;
    @FXML private Text questionText;
    @FXML private VBox optionsContainer;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button submitButton;

    private List<QuizQuestion> questions;
    private int currentIndex = 0;
    private Map<Integer, Integer> userAnswers = new HashMap<>();
    private Timeline timer;
    private int timeRemaining = 3600; // 60 minutes in seconds

    @FXML
    public void initialize() {
        loadQuestions();
        setupTimer();
        showQuestion(0);
        updateNavigationButtons();
    }

    private void loadQuestions() {
        // Initialize with sample questions (replace with your actual questions)
        questions = new ArrayList<>();
        // Add your questions here
        QuizQuestionDatabase database = new QuizQuestionDatabase();
        questions = database.getRandomQuestions();
    }

    private void setupTimer() {
        timer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                timeRemaining--;
                updateTimerLabel();
                if (timeRemaining <= 0) {
                    handleTimeUp();
                }
            })
        );
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateTimerLabel() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("Time Remaining: %02d:%02d", minutes, seconds));
    }

    private void showQuestion(int index) {
        QuizQuestion question = questions.get(index);
        questionNumberLabel.setText(String.format("Question %d of %d", index + 1, questions.size()));
        questionText.setText(question.getQuestionText());
        
        optionsContainer.getChildren().clear();
        ToggleGroup group = new ToggleGroup();
        
        String[] options = question.getOptions();
        for (int i = 0; i < options.length; i++) {
            RadioButton rb = new RadioButton(options[i]);
            rb.setToggleGroup(group);
            rb.setWrapText(true);
            final int answerIndex = i;
            rb.setOnAction(e -> userAnswers.put(currentIndex, answerIndex));
            
            // Select if previously answered
            if (userAnswers.containsKey(index) && userAnswers.get(index) == i) {
                rb.setSelected(true);
            }
            
            optionsContainer.getChildren().add(rb);
        }
    }

    @FXML
    private void handlePrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            showQuestion(currentIndex);
            updateNavigationButtons();
        }
    }

    @FXML
    private void handleNext() {
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            showQuestion(currentIndex);
            updateNavigationButtons();
        }
    }

    @FXML
    private void handleSubmit() {
        timer.stop();
        
        int correctAnswers = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers.containsKey(i) && 
                userAnswers.get(i) == questions.get(i).getCorrectAnswerIndex()) {
                correctAnswers++;
            }
        }

        double percentage = (double) correctAnswers / questions.size() * 100;
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Results");
        alert.setHeaderText("Quiz Complete!");
        alert.setContentText(String.format(
            "Score: %.1f%%\nCorrect Answers: %d/%d",
            percentage, correctAnswers, questions.size()
        ));
        alert.showAndWait();
    }

    private void handleTimeUp() {
        timer.stop();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Time's Up");
        alert.setHeaderText("Quiz time has expired!");
        alert.setContentText("Your answers will be submitted automatically.");
        alert.showAndWait();
        handleSubmit();
    }

    private void updateNavigationButtons() {
        previousButton.setDisable(currentIndex == 0);
        nextButton.setDisable(currentIndex == questions.size() - 1);
        submitButton.setDisable(questions.isEmpty());
    }
}