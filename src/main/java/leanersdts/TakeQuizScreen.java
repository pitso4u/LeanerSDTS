package leanersdts;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.temporal.ChronoUnit;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import leanersdts.ReviewScreen;
import javafx.scene.Node;

public class TakeQuizScreen implements ControlledScreen {

    private static final Logger LOGGER = Logger.getLogger(TakeQuizScreen.class.getName());
    private static final long QUIZ_DURATION_MINUTES = 60;

    // FXML Components from the new design
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label timerLabel;
    @FXML
    private Text questionLabel;
    @FXML
    private ImageView questionImageView;
    @FXML
    private VBox optionsContainer;
    @FXML
    private ToggleGroup optionsGroup;
    @FXML
    private Button previousButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button skipButton;
    @FXML
    private Button reviewButton;

    private ScreenManager screenManager;
    private List<QuizQuestion> shuffledQuestions;
    private List<Integer> selectedAnswers;
    private int currentQuestionIndex = 0;
    private boolean quizDataLoaded = false;

    private Timeline quizTimer;
    private Instant quizEndTime;
    private long quizStartTimeMillis;
    private ReviewScreenController reviewController;

    @Override
    public void setScreenParent(ScreenManager screenParent) {
        this.screenManager = screenParent;
        // reviewController will be initialized lazily in submitQuiz()
        LOGGER.info("[TakeQuizScreen setScreenParent] ScreenManager set. ReviewController will be fetched on demand.");
    }

    @FXML
    public void initialize() {
        LOGGER.info("[TakeQuizScreen] Initializing new quiz screen UI.");
        // Set initial state for new UI components
        progressBar.setProgress(0.0);
        timerLabel.setText("--:--");
        questionLabel.setText("Loading quiz, please wait...");
        optionsContainer.getChildren().clear();
        setNavButtonsDisable(true); // Disable all navigation buttons initially
    }

    public void loadQuizDataAndStart() {
        if (quizDataLoaded) {
            LOGGER.info("[TakeQuizScreen] Quiz data already loaded. Resuming quiz.");
            return; // Avoid reloading data
        }

        LOGGER.info("[TakeQuizScreen] Loading quiz data and starting quiz.");
        try {
            QuizQuestionDatabase database = new QuizQuestionDatabase();
            shuffledQuestions = database.getRandomQuestions();

            if (shuffledQuestions == null || shuffledQuestions.isEmpty()) {
                LOGGER.severe("[TakeQuizScreen] No questions were loaded from the server.");
                showErrorAlert("Quiz Error", "Could not load any questions. Please try again later.");
                return;
            }

            // Initialize selectedAnswers list with -1 (unanswered)
            selectedAnswers = new ArrayList<>();
            for (int i = 0; i < shuffledQuestions.size(); i++) {
                selectedAnswers.add(-1);
            }

            currentQuestionIndex = 0;
            loadQuestion(currentQuestionIndex);
            startQuizTimer(); // Defined below
            updateNavigationButtonStates();
            quizDataLoaded = true;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[TakeQuizScreen] A critical error occurred while loading the quiz.", e);
            AlertMaker.showErrorMessage("Fatal Quiz Error", "An unexpected error occurred: " + e.getMessage()); // Corrected call
            quizDataLoaded = false;
        }
    }

    private void loadQuestion(int index) {
        if (index < 0 || index >= shuffledQuestions.size())
            return;

        QuizQuestion question = shuffledQuestions.get(index);
        questionLabel.setText(question.getQuestion());

        // Handle image visibility
        String imageUrl = question.getImageUrl();
        LOGGER.info("[TakeQuizScreen] Attempting to load image. URL received: " + imageUrl);

        if (question.hasImage() && imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image questionImage = new Image(imageUrl, true); // Load in background

                questionImage.errorProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        LOGGER.log(Level.SEVERE, "Error loading image from URL: " + imageUrl,
                                questionImage.getException());
                    }
                });

                questionImageView.setImage(questionImage);
                questionImageView.setVisible(true);
                // Explicitly set fit height to preserve aspect ratio, or manage layout better
                questionImageView.setFitHeight(150); // Example height, adjust as needed
                questionImageView.setPreserveRatio(true);

                LOGGER.info("[TakeQuizScreen] Image loading initiated for URL: " + imageUrl);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE,
                        "A critical exception occurred while creating the Image object for URL: " + imageUrl, e);
                questionImageView.setImage(null);
                questionImageView.setVisible(false);
            }
        } else {
            LOGGER.info("[TakeQuizScreen] No valid image URL provided for this question. Hiding ImageView.");
            questionImageView.setImage(null);
            questionImageView.setVisible(false);
        }

        // Dynamically create and add styled ToggleButtons for options
        optionsContainer.getChildren().clear();
        optionsGroup.getToggles().clear();

        String[] options = question.getOptions();
        for (int i = 0; i < options.length; i++) {
            ToggleButton optionButton = new ToggleButton(options[i]);
            optionButton.setToggleGroup(optionsGroup);
            optionButton.getStyleClass().add("option-toggle-button");
            optionButton.setWrapText(true); // Ensure long text wraps
            optionButton.setPrefHeight(Control.USE_COMPUTED_SIZE); // Let the button determine its own height
            optionButton.setUserData(i); // Store the option's original index
            optionsContainer.getChildren().add(optionButton);
        }

        // Restore previously selected answer for this question
        progressBar.setProgress((double) (index + 1) / shuffledQuestions.size()); // Moved here
        int previousAnswer = selectedAnswers.get(index);
        if (previousAnswer != -1) {
            for (Toggle toggle : optionsGroup.getToggles()) {
                if ((Integer) toggle.getUserData() == previousAnswer) {
                    toggle.setSelected(true);
                    break;
                }
            }
        }

        // Update progress bar
        progressBar.setProgress((double) (index + 1) / shuffledQuestions.size());
    }

    private void recordAnswer() {
        Toggle selectedToggle = optionsGroup.getSelectedToggle();
        if (selectedToggle != null) {
            int selectedIndex = (Integer) selectedToggle.getUserData();
            selectedAnswers.set(currentQuestionIndex, selectedIndex);
        } else {
            selectedAnswers.set(currentQuestionIndex, -1); // No answer selected
        }
    }

    @FXML
    private void handleNextButtonAction(ActionEvent event) {
        recordAnswer();
        if (currentQuestionIndex < shuffledQuestions.size() - 1) {
            currentQuestionIndex++;
            loadQuestion(currentQuestionIndex);
            updateNavigationButtonStates();
        }
    }

    @FXML
    private void handlePreviousButtonAction(ActionEvent event) {
        recordAnswer();
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            loadQuestion(currentQuestionIndex);
            updateNavigationButtonStates();
        }
    }

    @FXML
    private void handleSkipButtonAction(ActionEvent event) {
        selectedAnswers.set(currentQuestionIndex, -1); // Mark as skipped
        if (currentQuestionIndex < shuffledQuestions.size() - 1) {
            currentQuestionIndex++;
            loadQuestion(currentQuestionIndex);
            updateNavigationButtonStates();
        }
    }

    @FXML
    private void handleReviewButtonAction(ActionEvent event) {
        // This button acts as the final submit.
        submitQuiz();
    }

    private void submitQuiz() {
        LOGGER.info("[TakeQuizScreen submitQuiz] Method started.");

        // Lazy initialization of reviewController
        if (this.reviewController == null) {
            LOGGER.info("[TakeQuizScreen submitQuiz] Attempting to lazily initialize ReviewScreenController.");
            if (screenManager == null) {
                LOGGER.severe("[TakeQuizScreen submitQuiz] ScreenManager is null. Cannot get ReviewScreenController.");
                AlertMaker.showErrorMessage("Critical Error", "ScreenManager not available. Cannot proceed to review."); // Corrected call
                return;
            }
            Node reviewScreenNode = screenManager.getScreen(LeanerSDTS.ReviewScreenID);
            LOGGER.info("[TakeQuizScreen submitQuiz] screenManager.getScreen(ReviewScreenID) returned: " + (reviewScreenNode == null ? "null" : reviewScreenNode.getClass().getName()));
            if (reviewScreenNode instanceof ReviewScreen) {
                ReviewScreen reviewScreen = (ReviewScreen) reviewScreenNode;
                this.reviewController = reviewScreen.getControllerInstance();
                if (this.reviewController == null) {
                    LOGGER.severe("[TakeQuizScreen submitQuiz] Lazily fetched ReviewScreen component returned a null controller instance.");
                } else {
                    LOGGER.info("[TakeQuizScreen submitQuiz] Successfully lazily initialized ReviewScreenController.");
                }
            } else {
                LOGGER.severe("[TakeQuizScreen submitQuiz] Could not obtain ReviewScreen component for lazy initialization. Expected leanersdts.ReviewScreen, Got: " + (reviewScreenNode != null ? reviewScreenNode.getClass().getName() : "null"));
            }
        }

        if (quizTimer != null) {
            quizTimer.stop();
        }
        long timeTakenMillis = System.currentTimeMillis() - quizStartTimeMillis; // Calculate time taken
        LOGGER.info("[TakeQuizScreen submitQuiz] Quiz timer stopped. Time elapsed: " + timeTakenMillis + " ms");

        recordAnswer(); // Ensure the last answer is recorded

        if (shuffledQuestions == null || shuffledQuestions.isEmpty()) {
            LOGGER.warning("[TakeQuizScreen submitQuiz] No questions to submit.");
            showErrorAlert("Submit Error", "There are no questions to submit.");
            return;
        }

        // DO NOT grade questions here. Grading will happen after the pre-submission review.

        // Navigate to Review Screen for pre-submission review
        if (this.reviewController != null) {
            LOGGER.info("[TakeQuizScreen submitQuiz] ReviewController is initialized. Navigating to review screen for pre-submission review.");
            // Pass the original shuffledQuestions (ungraded) and selectedAnswers.
            // ReviewScreenController will handle displaying these for user modification.
            this.reviewController.setData(new ArrayList<>(shuffledQuestions), new ArrayList<>(selectedAnswers), timeTakenMillis);
            screenManager.setScreen(LeanerSDTS.ReviewScreenID);
        } else {
            LOGGER.severe("[TakeQuizScreen submitQuiz] ReviewScreenController is STILL NOT initialized after lazy attempt. Cannot navigate to review screen.");
            showErrorAlert("Navigation Error", "Could not prepare the review screen. Please contact support.");
        }
    }

    private void startQuizTimer() {
        quizStartTimeMillis = System.currentTimeMillis();
        quizEndTime = Instant.now().plus(QUIZ_DURATION_MINUTES, ChronoUnit.MINUTES);
        if (quizTimer != null) {
            quizTimer.stop();
        }
        quizTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimer()));
        quizTimer.setCycleCount(Timeline.INDEFINITE);
        quizTimer.play();
        LOGGER.info("[TakeQuizScreen] Quiz timer started.");
    }

    private void updateTimer() {
        if (quizEndTime == null) {
            LOGGER.warning("[TakeQuizScreen] quizEndTime is null in updateTimer.");
            if (quizTimer != null) quizTimer.stop();
            return;
        }
        java.time.Duration remainingTime = java.time.Duration.between(Instant.now(), quizEndTime);

        if (remainingTime.isNegative() || remainingTime.isZero()) {
            timerLabel.setText("00:00");
            if (quizTimer != null) {
                quizTimer.stop();
            }
            showErrorAlert("Time's Up!", "The quiz time has expired. Your answers will now be submitted.");
            submitQuiz(); // submitQuiz signature updated, no longer throws IOException directly
        } else {
            long totalRemainingSeconds = remainingTime.getSeconds(); // Java 8 compatible
            long minutes = totalRemainingSeconds / 60;
            long seconds = totalRemainingSeconds % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    private void updateNavigationButtonStates() {
        setNavButtonsDisable(false); // Enable all by default, then selectively disable

        if (shuffledQuestions == null || shuffledQuestions.isEmpty()) {
            LOGGER.info("[TakeQuizScreen] No questions loaded, disabling navigation buttons.");
            setNavButtonsDisable(true); // Disable all if no questions
            if (reviewButton != null) reviewButton.setText("Review & Submit");
            return;
        }

        if (previousButton != null) previousButton.setDisable(currentQuestionIndex == 0);
        
        boolean isLastQuestion = (currentQuestionIndex == shuffledQuestions.size() - 1);
        if (nextButton != null) nextButton.setDisable(isLastQuestion);
        if (skipButton != null) skipButton.setDisable(isLastQuestion);
        if (reviewButton != null) reviewButton.setText(isLastQuestion ? "Submit Quiz" : "Review & Submit");
    }

    private void setNavButtonsDisable(boolean disable) {
        if (previousButton != null) previousButton.setDisable(disable);
        if (nextButton != null) nextButton.setDisable(disable);
        if (skipButton != null) skipButton.setDisable(disable);
        if (reviewButton != null) reviewButton.setDisable(disable);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void runOnScreenChange() {}

    
    @Override
    public void cleanup() {
        if (quizTimer != null) {
            quizTimer.stop();
        }
        quizDataLoaded = false;
    }
}