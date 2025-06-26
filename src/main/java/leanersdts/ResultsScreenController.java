package leanersdts;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResultsScreenController implements ControlledScreen {

    private static final Logger LOGGER = Logger.getLogger(ResultsScreenController.class.getName());

    @FXML
    private Label scoreLabel;
    @FXML
    private Label timeTakenLabel;
    @FXML
    private Button retakeQuizButton;
    @FXML
    private Button reviewAnswersButton;
    @FXML
    private Button exitToDashboardButton;

    private ScreenManager screenManager;
    private List<QuizQuestion> questionsForReview;
    private long timeTakenMillis; // Added field to store time taken

    @Override
    public void setScreenParent(ScreenManager screenParent) {
        this.screenManager = screenParent;
    }

    public void setData(int score, int totalQuestions, long timeMillis, List<QuizQuestion> questions) {
        this.questionsForReview = questions;
        this.timeTakenMillis = timeMillis; // Store timeMillis

        double percentage = totalQuestions > 0 ? ((double) score / totalQuestions) * 100 : 0;
        scoreLabel.setText(String.format("Your Score: %d/%d (%.2f%%)", score, totalQuestions, percentage));

        String timeFormatted = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeMillis),
                TimeUnit.MILLISECONDS.toSeconds(timeMillis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMillis))
        );
        timeTakenLabel.setText("Time Taken: " + timeFormatted);
        
        // Disable review button if there's nothing to review (e.g., direct navigation from an error)
        if (questionsForReview == null || questionsForReview.isEmpty()) {
            reviewAnswersButton.setDisable(true);
        }
    }

    @FXML
    private void handleRetakeQuizAction(ActionEvent event) {
        if (screenManager != null) {
            // Reset and navigate to TakeQuizScreen
            // TakeQuizScreen should re-initialize itself to fetch new questions
            screenManager.setScreen(LeanerSDTS.TakeQuizScreenID);
        } else {
            LOGGER.log(Level.SEVERE, "ScreenManager is null in ResultsScreenController.");
        }
    }

    @FXML
    private void handleReviewAnswersAction(ActionEvent event) {
        if (screenManager != null && questionsForReview != null && !questionsForReview.isEmpty()) {
            boolean loaded = screenManager.loadScreen(LeanerSDTS.ReviewScreenID, LeanerSDTS.ReviewScreenFile);
            if (loaded) {
                ReviewScreen reviewScreenNode = (ReviewScreen) screenManager.getScreen(LeanerSDTS.ReviewScreenID);
                ReviewScreenController reviewController = null;
                if (reviewScreenNode != null) {
                    reviewController = reviewScreenNode.getControllerInstance();
                } else {
                    LOGGER.log(Level.SEVERE, "Could not retrieve ReviewScreen node from ScreenManager.");
                }
                if (reviewController != null) {
                    // ResultsScreen stores timeTakenMillis, pass it back if user reviews from results.
                    // Assuming this.timeTakenMillis holds the original quiz duration for this results set.
                    // Call setDataForPostResults for read-only, graded review mode.
                    reviewController.setDataForPostResults(questionsForReview, this.timeTakenMillis);
                    screenManager.setScreen(LeanerSDTS.ReviewScreenID);
                } else {
                    LOGGER.log(Level.SEVERE, "Could not retrieve ReviewScreenController.");
                    // Optionally show an error alert to the user
                }
            } else {
                LOGGER.log(Level.SEVERE, "Could not load ReviewScreen.");
                // Optionally show an error alert to the user
            }
        } else {
            LOGGER.log(Level.WARNING, "Cannot review answers: ScreenManager is null or no review data available.");
            if (reviewAnswersButton != null) reviewAnswersButton.setDisable(true);
        }
    }

    @FXML
    private void handleExitToDashboardAction(ActionEvent event) {
        if (screenManager != null) {
            screenManager.setScreen(LeanerSDTS.DashboardScreenID);
        } else {
            LOGGER.log(Level.SEVERE, "ScreenManager is null in ResultsScreenController.");
        }
    }

    @Override
    public void runOnScreenChange() {
        // Optional: Logic to run when this screen becomes active
        // For example, re-enable buttons if they were disabled due to no data
        if (reviewAnswersButton != null && questionsForReview != null && !questionsForReview.isEmpty()) {
            reviewAnswersButton.setDisable(false);
        }
    }

    @Override
    public void cleanup() {
        // Optional: Cleanup resources when screen is changed
        this.questionsForReview = null;
    }
}
