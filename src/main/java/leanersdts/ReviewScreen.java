package leanersdts;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXMLLoader;
import java.io.IOException;
import leanersdts.ReviewScreenController;

public class ReviewScreen extends VBox implements ControlledScreen {
    private ReviewScreenController controller;

    private static final Logger logger = LoggerFactory.getLogger(ReviewScreen.class);

    private ScreenManager screenManager;
    private List<QuizQuestion> allQuestions;
    private List<QuizQuestion> questionsToReview;
    private int currentQuestionIndex = 0;
    private ReviewMode reviewMode;

    public ReviewScreen() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ReviewScreen.fxml"));
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
            Object loadedController = fxmlLoader.getController();
            logger.info("[ReviewScreen Constructor] FXMLLoader.getController() returned: " + (loadedController == null ? "null" : loadedController.getClass().getName()));
            if (loadedController instanceof ReviewScreenController) {
                this.controller = (ReviewScreenController) loadedController;
                logger.info("[ReviewScreen Constructor] Successfully cast and assigned ReviewScreenController. this.controller is now: " + (this.controller == null ? "null" : "initialized"));
            } else if (loadedController == null) {
                logger.error("[ReviewScreen Constructor] FXMLLoader returned a null controller for ReviewScreen.fxml. Check fx:controller attribute in FXML.");
                this.controller = null;
            } else {
                logger.error("[ReviewScreen Constructor] FXMLLoader returned a controller of unexpected type: " + loadedController.getClass().getName() + ". Expected leanersdts.ReviewScreenController.");
                this.controller = null;
            }
        } catch (IOException exception) {
            logger.error("Failed to load ReviewScreen.fxml", exception);
            throw new RuntimeException(exception);
        }
    }

    public enum ReviewMode {
        PRE_SUBMISSION, // User can change answers
        POST_SUBMISSION // Read-only review of results
    }

    // --- FXML Elements ---
    @FXML private Label questionNumberLabel;
    @FXML private TextFlow questionTextFlow;
    @FXML private ImageView questionImageView;
    @FXML private VBox optionsContainer;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Button backButton;
    @FXML private Button finalSubmitButton;
    @FXML private CheckBox showIncorrectOnlyCheckBox;
    @FXML private Slider textSizeSlider;

    public void setData(List<QuizQuestion> questions, ReviewMode mode) {
        this.allQuestions = questions;
        this.reviewMode = mode;
        this.questionsToReview = allQuestions; // Initially, show all questions
        this.currentQuestionIndex = 0;
        
        setupScreenForMode();
        displayCurrentQuestion();
    }

    private void setupScreenForMode() {
        if (reviewMode == ReviewMode.PRE_SUBMISSION) {
            showIncorrectOnlyCheckBox.setVisible(false);
            showIncorrectOnlyCheckBox.setSelected(false);
            finalSubmitButton.setVisible(true);
        } else { // POST_SUBMISSION
            showIncorrectOnlyCheckBox.setVisible(true);
            finalSubmitButton.setVisible(false);
        }
    }

    private void displayCurrentQuestion() {
        if (questionsToReview == null || questionsToReview.isEmpty()) {
            questionTextFlow.getChildren().clear();
            questionTextFlow.getChildren().add(new Text("No questions to display."));
            optionsContainer.getChildren().clear();
            questionImageView.setImage(null);
            questionNumberLabel.setText("0/0");
            previousButton.setDisable(true);
            nextButton.setDisable(true);
            return;
        }

        QuizQuestion question = questionsToReview.get(currentQuestionIndex);

        Text text = new Text(question.getQuestion());
        questionTextFlow.getChildren().setAll(text);
        
        if (textSizeSlider != null) {
            text.styleProperty().bind(javafx.beans.binding.Bindings.concat("-fx-font-size: ", textSizeSlider.valueProperty().asString(), "pt;"));
        }

        if (question.hasImage() && question.getImageUrl() != null && !question.getImageUrl().isEmpty()) {
            try {
                Image img = new Image(question.getImageUrl(), true);
                questionImageView.setImage(img);
                questionImageView.setVisible(true);
            } catch (Exception e) {
                logger.error("Failed to load image: {}", question.getImageUrl(), e);
                questionImageView.setImage(null);
                questionImageView.setVisible(false);
            }
        } else {
            questionImageView.setImage(null);
            questionImageView.setVisible(false);
        }

        optionsContainer.getChildren().clear();
        ToggleGroup toggleGroup = new ToggleGroup();
        String[] options = question.getOptions();
        for (int i = 0; i < options.length; i++) {
            RadioButton rb = new RadioButton(options[i]);
            rb.setToggleGroup(toggleGroup);
            rb.setUserData(i);
            optionsContainer.getChildren().add(rb);
            
            if (question.getUserAnswerIndex() == i) {
                rb.setSelected(true);
            }

            if (reviewMode == ReviewMode.POST_SUBMISSION) {
                rb.setDisable(true);
                if (question.getCorrectAnswerIndex() == i) {
                    rb.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                }
                if (question.getUserAnswerIndex() == i && !question.isCorrect()) {
                     rb.setStyle("-fx-text-fill: red;");
                }
            }
        }
        
        if (reviewMode == ReviewMode.PRE_SUBMISSION) {
            toggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle != null) {
                    int selectedIndex = (int) newToggle.getUserData();
                    question.setUserAnswerIndex(selectedIndex);
                }
            });
        }

        questionNumberLabel.setText(String.format("Question %d of %d", currentQuestionIndex + 1, questionsToReview.size()));
        previousButton.setDisable(currentQuestionIndex == 0);
        nextButton.setDisable(currentQuestionIndex == questionsToReview.size() - 1);
    }

    @FXML
    private void handlePreviousAction() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayCurrentQuestion();
        }
    }

    @FXML
    private void handleNextAction() {
        if (currentQuestionIndex < questionsToReview.size() - 1) {
            currentQuestionIndex++;
            displayCurrentQuestion();
        }
    }

    @FXML
    private void handleBackAction() {
        logger.info("Back button clicked.");
        // This should navigate to the QuizSummaryScreen or Dashboard
        screenManager.setScreen("QuizSummaryScreen");
    }
    
    @FXML
    private void handleFinalSubmitAction() {
        logger.info("Final Submit button clicked.");
        // TODO: Implement quiz grading and submission logic
    }

    @FXML
    private void handleFilterAction() {
        if (showIncorrectOnlyCheckBox.isSelected()) {
            questionsToReview = allQuestions.stream()
                .filter(q -> !q.isCorrect())
                .collect(Collectors.toList());
        } else {
            questionsToReview = allQuestions;
        }
        currentQuestionIndex = 0;
        displayCurrentQuestion();
    }

    @Override
    public void setScreenParent(ScreenManager screenParent) {
        this.screenManager = screenParent;
        if (this.controller != null) {
            this.controller.setScreenParent(screenParent);
        } else {
            logger.error("[ReviewScreen setScreenParent] Controller is null. Cannot set ScreenManager on it.");
        }
    }

    @Override
    public void runOnScreenChange() {
        logger.info("ReviewScreen is now visible.");
    }

    public ReviewScreenController getControllerInstance() {
        logger.info("[ReviewScreen getControllerInstance] Returning controller: " + (this.controller == null ? "null" : "initialized"));
        return this.controller;
    }

    @Override
    public void cleanup() {
        this.allQuestions = null;
        this.questionsToReview = null;
    }
}
