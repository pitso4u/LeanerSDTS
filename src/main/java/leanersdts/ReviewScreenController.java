package leanersdts;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;

public class ReviewScreenController implements ControlledScreen {

    @FXML
    private VBox reviewContainer; // This will hold all question boxes
    @FXML
    private CheckBox showIncorrectOnlyCheckBox;
    @FXML
    private Button finalSubmitButton; // Assuming fx:id="finalSubmitButton" for the main submit button
    @FXML
    private Button backButton; // Assuming fx:id="backButton"

    private ScreenManager screenManager;
    private List<QuizQuestion> shuffledQuestions;

    private long timeTakenMillis;
    private boolean isPreSubmissionReviewMode = true; // Default to pre-submission interactive review

    // setData for the initial review (pre-submission, interactive)
    public void setData(List<QuizQuestion> questions, long timeTakenMillis) {
        this.shuffledQuestions = new ArrayList<>(questions); // Use copies to avoid modifying original lists from TakeQuizScreen
        this.timeTakenMillis = timeTakenMillis;
        this.isPreSubmissionReviewMode = true;
        System.out.println("ReviewScreenController: setData called for Pre-Submission Review.");
        loadReviewContent();
        updateButtonStates();
    }

    // setData for the post-results review (read-only, graded)
    public void setDataForPostResults(List<QuizQuestion> gradedQuestions, long timeTakenMillis) {
        this.shuffledQuestions = new ArrayList<>(gradedQuestions);
        this.timeTakenMillis = timeTakenMillis; // This might not be relevant here or could be quiz completion time
        this.isPreSubmissionReviewMode = false;
        System.out.println("ReviewScreenController: setDataForPostResults called.");
        loadReviewContent();
        updateButtonStates();
    }

    private void loadReviewContent() {
        if (reviewContainer == null) {
            System.err.println("ReviewScreenController: reviewContainer is null.");
            return;
        }
        if (shuffledQuestions == null) {
            System.err.println("ReviewScreenController: Cannot load review content. Questions list is null.");
            reviewContainer.getChildren().clear(); // Clear even if data is null to avoid stale display
            Label errorLabel = new Label("Could not load review content. Data is missing.");
            reviewContainer.getChildren().add(errorLabel);
            return;
        }
        reviewContainer.getChildren().clear();

        boolean filterIncorrectOnly = !isPreSubmissionReviewMode && (showIncorrectOnlyCheckBox != null) && showIncorrectOnlyCheckBox.isSelected();

        int displayIndex = 0;
        for (int i = 0; i < shuffledQuestions.size(); i++) {
            QuizQuestion question = shuffledQuestions.get(i);

            if (!isPreSubmissionReviewMode) { // Filtering logic only for post-results review
                if (filterIncorrectOnly && question.isCorrect() && !question.isSkipped()) {
                    continue; // Skip correct questions if filter is on
                }
            }

            displayIndex++;
            VBox questionBox = new VBox(10);
            questionBox.getStyleClass().add("review-question-box");
            questionBox.setPadding(new Insets(10, 15, 10, 15));

            Label questionLabelText = new Label(displayIndex + ". " + question.getQuestion());
            questionLabelText.setWrapText(true);
            questionLabelText.getStyleClass().add("review-question-label");
            questionBox.getChildren().add(questionLabelText);

            if (question.hasImage() && question.getImageUrl() != null && !question.getImageUrl().isEmpty()) {
                try {
                    ImageView imageView = new ImageView(new Image(question.getImageUrl(), true)); // true for background loading
                    imageView.setFitHeight(150); // Adjust as needed
                    imageView.setPreserveRatio(true);
                    VBox.setMargin(imageView, new Insets(5, 0, 5, 0));
                    questionBox.getChildren().add(imageView);
                } catch (Exception e) {
                    System.err.println("Error loading image for review: " + question.getImageUrl() + " - " + e.getMessage());
                }
            }

            if (isPreSubmissionReviewMode) {
                ToggleGroup optionsGroup = new ToggleGroup();
                VBox optionsVBox = new VBox(5); // VBox to hold radio buttons for this question
                optionsVBox.setPadding(new Insets(5, 0, 0, 10));
                String[] options = question.getOptions();
                for (int j = 0; j < options.length; j++) {
                    RadioButton rb = new RadioButton(options[j]);
                    rb.setUserData(j); // Store option index
                    rb.setToggleGroup(optionsGroup);
                    rb.setWrapText(true);
                    rb.getStyleClass().add("review-option-radio");
                    if (question.getUserAnswerIndex() == j) {
                        rb.setSelected(true);
                    }
                    optionsVBox.getChildren().add(rb);
                }
                questionBox.getChildren().add(optionsVBox);
            } else {
                // Post-results review: Display user's answer and correct/incorrect styling
                int finalAnswerIndex = question.getUserAnswerIndex();
                String userAnswerText = "Not Answered";
                if (finalAnswerIndex != -1 && finalAnswerIndex < question.getOptions().length) {
                    userAnswerText = question.getOptions()[finalAnswerIndex];
                } else if (question.isSkipped()) {
                    userAnswerText = "Skipped";
                }

                Label userAnswerDisplay = new Label("Your answer: " + userAnswerText);
                userAnswerDisplay.setWrapText(true);
                userAnswerDisplay.getStyleClass().add("review-user-answer");

                if (question.isSkipped()) {
                    userAnswerDisplay.getStyleClass().add("skipped-answer-label");
                } else if (finalAnswerIndex != -1) {
                    if (question.isCorrect()) {
                        userAnswerDisplay.getStyleClass().add("correct-answer-label");
                    } else {
                        userAnswerDisplay.getStyleClass().add("incorrect-answer-label");
                    }
                }
                questionBox.getChildren().add(userAnswerDisplay);

                if (!question.isCorrect() || question.isSkipped()) {
                    if (question.getCorrectAnswerIndex() >= 0 && question.getCorrectAnswerIndex() < question.getOptions().length) {
                        Label correctAnswerDisplay = new Label("Correct answer: " + question.getOptions()[question.getCorrectAnswerIndex()]);
                        correctAnswerDisplay.getStyleClass().add("correct-answer-reveal-label");
                        correctAnswerDisplay.setWrapText(true);
                        questionBox.getChildren().add(correctAnswerDisplay);
                    }
                }
            }
            reviewContainer.getChildren().add(questionBox);

            // Add separator if not the last question to be displayed
            boolean addSeparator = false;
            if (i < shuffledQuestions.size() - 1) { // If there's at least one more question in the original list
                if (isPreSubmissionReviewMode) {
                    addSeparator = true; // In pre-submission, all questions are shown sequentially
                } else {
                    // In post-submission, check if the *next* actual question in the list would be displayed based on filter
                    for (int k = i + 1; k < shuffledQuestions.size(); k++) {
                        QuizQuestion nextQuestionToCheck = shuffledQuestions.get(k);
                        if (filterIncorrectOnly && nextQuestionToCheck.isCorrect() && !nextQuestionToCheck.isSkipped()) {
                            continue; // This next one would be filtered out, check the one after
                        }
                        addSeparator = true; // Found a subsequent question that will be displayed
                        break;
                    }
                }
            }
            if (addSeparator) {
                Separator separator = new Separator(javafx.geometry.Orientation.HORIZONTAL);
                VBox.setMargin(separator, new Insets(10, 0, 10, 0));
                reviewContainer.getChildren().add(separator);
            }
        }
    }

    @FXML
    private void handleFinalSubmitAction(ActionEvent event) {
        if (!isPreSubmissionReviewMode) {
            System.out.println("handleFinalSubmitAction called in post-results mode. This should not happen or button should be disabled/repurposed.");
            // Optionally navigate to results screen or dashboard
            // screenManager.setScreen(LeanerSDTS.ResultsScreenID);
            return;
        }

                if (shuffledQuestions == null) {
            System.err.println("Error: Quiz data not available for final submission.");
            AlertMaker.showErrorMessage("Submission Error", "Quiz data is missing. Cannot submit.");
            return;
        }

        // 1. Collect final answers from the interactive RadioButtons and update question objects
        int questionCounter = 0;
        for (javafx.scene.Node node : reviewContainer.getChildren()) {
            if (node instanceof VBox) { // Each VBox is a questionBox
                if (questionCounter >= shuffledQuestions.size()) break;

                VBox questionBox = (VBox) node;
                ToggleGroup group = null;
                for (javafx.scene.Node childNode : questionBox.getChildren()) {
                    if (childNode instanceof VBox) {
                        VBox optionsVBox = (VBox) childNode;
                        if (!optionsVBox.getChildren().isEmpty() && optionsVBox.getChildren().get(0) instanceof RadioButton) {
                            group = ((RadioButton) optionsVBox.getChildren().get(0)).getToggleGroup();
                            break;
                        }
                    }
                }

                QuizQuestion question = shuffledQuestions.get(questionCounter);
                if (group != null && group.getSelectedToggle() != null) {
                    question.setUserAnswerIndex((Integer) group.getSelectedToggle().getUserData());
                } else {
                    question.setUserAnswerIndex(-1); // No answer selected
                }
                questionCounter++;
            }
        }

        // 2. Perform Grading
        int score = 0;
        for (int i = 0; i < shuffledQuestions.size(); i++) {
            QuizQuestion question = shuffledQuestions.get(i);
            int answerIdx = question.getUserAnswerIndex();
            question.setUserAnswerIndex(answerIdx); // Set the final user answer on the question object
            if (answerIdx != -1) { // If an answer was made
                question.setCorrect(answerIdx == question.getCorrectAnswerIndex());
                question.setSkipped(false);
            } else { // If skipped
                question.setCorrect(false);
                question.setSkipped(true);
            }
            if (question.isCorrect()) {
                score++;
            }
        }

        // 3. Navigate to Results Screen
        if (screenManager != null) {
            boolean loaded = screenManager.loadScreen(LeanerSDTS.ResultsScreenID, LeanerSDTS.ResultsScreenFile);
            if (loaded) {
                ResultsScreenController resultsController = (ResultsScreenController) screenManager.getController(LeanerSDTS.ResultsScreenID);
                if (resultsController != null) {
                    resultsController.setData(score, shuffledQuestions.size(), this.timeTakenMillis, this.shuffledQuestions);
                    screenManager.setScreen(LeanerSDTS.ResultsScreenID);
                } else {
                    System.err.println("Error: Could not retrieve ResultsScreenController after final submission.");
                    AlertMaker.showErrorMessage("Navigation Error", "Could not load results screen controller.");
                }
            } else {
                System.err.println("Error: Could not load ResultsScreen after final submission.");
                AlertMaker.showErrorMessage("Navigation Error", "Could not load results screen.");
            }
        } else {
            System.err.println("Error: ScreenManager is null in ReviewScreenController during final submission.");
            AlertMaker.showErrorMessage("Critical Error", "ScreenManager is not available.");
        }
    }

    private void updateButtonStates() {
        if (showIncorrectOnlyCheckBox != null) {
            showIncorrectOnlyCheckBox.setVisible(!isPreSubmissionReviewMode);
            showIncorrectOnlyCheckBox.setDisable(isPreSubmissionReviewMode);
        }
        if (finalSubmitButton != null) {
            finalSubmitButton.setVisible(isPreSubmissionReviewMode);
            finalSubmitButton.setDisable(!isPreSubmissionReviewMode);
            finalSubmitButton.setText(isPreSubmissionReviewMode ? "Submit Final Answers" : "N/A"); // Or hide if not pre-submission
        }
        // The main 'Back' button might also change its text or target based on mode
        // if (backButton != null) { backButton.setText(isPreSubmissionReviewMode ? "Back to Quiz" : "Back to Results"); }
    }

    @FXML
    private void handleBackAction(ActionEvent event) {
        if (isPreSubmissionReviewMode) {
            // In pre-submission, 'Back' could go to Dashboard or TakeQuizScreen (if we want to allow returning to quiz taking)
            // For simplicity, let's make it go to Dashboard. Or show a confirmation dialog.
            // Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit review? Your changes here won't be saved yet.", ButtonType.YES, ButtonType.NO);
            // confirmation.showAndWait().ifPresent(response -> {
            //     if (response == ButtonType.YES) {
            //         screenManager.setScreen(LeanerSDTS.DashboardScreenID);
            //     }
            // });
            screenManager.setScreen(LeanerSDTS.DashboardScreenID); // Simplified: goes to dashboard
        } else {
            // In post-results review, 'Back' should go to the Results Screen
            screenManager.setScreen(LeanerSDTS.ResultsScreenID);
        }
    }

    @Override
    public void setScreenParent(ScreenManager screenParent) {
        this.screenManager = screenParent;
    }

    @Override
    public void runOnScreenChange() {
        // This method is called when the screen becomes visible.
        // We ensure buttons and checkbox visibility are correctly set based on the mode.
        updateButtonStates();
        // Note: loadReviewContent() is called by setData methods, so content should be fresh.
    }

    @Override
    public void cleanup() {
        reviewContainer.getChildren().clear();
        shuffledQuestions = null;
    }

    @FXML
    private void handlePreviousAction(ActionEvent event) {
        System.out.println("[ReviewScreenController] 'Previous Question' button clicked. Functionality not implemented for review screen.");
        // This button might be hidden or disabled via FXML or in updateButtonStates for ReviewScreen
    }

    @FXML
    private void handleNextAction(ActionEvent event) {
        System.out.println("[ReviewScreenController] 'Next Question' button clicked. Functionality not implemented for review screen.");
        // This button might be hidden or disabled via FXML or in updateButtonStates for ReviewScreen
    }

    @FXML
    private void handleFilterAction(ActionEvent event) {
        if (isPreSubmissionReviewMode) {
            System.out.println("[ReviewScreenController] handleFilterAction called in pre-submission mode (should be disabled). Ignored.");
            return; // Checkbox should be hidden/disabled
        }
        // This is for post-results review mode
        System.out.println("[ReviewScreenController] handleFilterAction called (post-results mode). Checkbox selected: " + showIncorrectOnlyCheckBox.isSelected());
        loadReviewContent(); // Reload content based on new filter state
    }
}
