package leanersdts;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class QuizSummaryScreen extends VBox implements ControlledScreen {
    private static final Logger logger = LoggerFactory.getLogger(QuizSummaryScreen.class);

    // --- MINIMUM SCORES AND TOTALS ---
    private static final int SIGNS_TOTAL = 28;
    private static final int RULES_TOTAL = 28;
    private static final int CONTROLS_TOTAL = 8;
    private static final int SIGNS_MIN = 23;
    private static final int RULES_MIN = 22;
    private static final int CONTROLS_MIN = 6;

    private ScreenManager screenManager;
    private List<QuizQuestion> questions;

    // --- FXML UI Elements ---
    @FXML private Label overallResultMessage;
    @FXML private Label percentageScoreLabel;

    // Metric Cards
    @FXML private VBox signsCard;
    @FXML private VBox rulesCard;
    @FXML private VBox controlsCard;

    // Status Icons
    @FXML private ImageView signsStatusIcon;
    @FXML private ImageView rulesStatusIcon;
    @FXML private ImageView controlsStatusIcon;

    // Score Labels
    @FXML private Label signsScoreLabel;
    @FXML private Label rulesScoreLabel;
    @FXML private Label controlsScoreLabel;

    // Minimum Required Labels
    @FXML private Label signsMinLabel;
    @FXML private Label rulesMinLabel;
    @FXML private Label controlsMinLabel;

    // Buttons
    @FXML private Button reviewAllButton;
    @FXML private Button reviewIncorrectButton;
    @FXML private Button dashboardButton;

    // Lazy-loaded images
    private Image passIcon;
    private Image failIcon;
    
    // Initialize images safely
    private void initializeImages() {
        try {
            var passStream = QuizSummaryScreen.class.getClassLoader().getResourceAsStream("Images/happy_face.jpeg");
            if (passStream != null) {
                passIcon = new Image(passStream);
            } else {
                logger.error("Could not load happy_face.jpeg image resource");
                // Use a fallback or default image
            }
            
            var failStream = QuizSummaryScreen.class.getClassLoader().getResourceAsStream("Images/sad_face.jpeg");
            if (failStream != null) {
                failIcon = new Image(failStream);
            } else {
                logger.error("Could not load sad_face.jpeg image resource");
                // Use a fallback or default image
            }
        } catch (Exception e) {
            logger.error("Error loading image resources", e);
        }
    }

    public void setData(List<QuizQuestion> questions, long timeTaken) {
        this.questions = questions;
        updateDisplay();
    }

    private void updateDisplay() {
        // Initialize images if not already done
        if (passIcon == null || failIcon == null) {
            initializeImages();
        }
        
        if (questions == null || questions.isEmpty()) {
            logger.warn("No questions data available to display summary.");
            return;
        }

        // --- Calculate Scores ---
        int signsCorrect = 0;
        int rulesCorrect = 0;
        int controlsCorrect = 0;

        for (QuizQuestion q : questions) {
            if (q.isCorrect()) {
                switch (q.getCategory().toLowerCase()) {
                    case "signs":
                        signsCorrect++;
                        break;
                    case "rules":
                        rulesCorrect++;
                        break;
                    case "controls":
                        controlsCorrect++;
                        break;
                }
            }
        }

        // --- Determine Pass/Fail Status ---
        boolean signsPassed = signsCorrect >= SIGNS_MIN;
        boolean rulesPassed = rulesCorrect >= RULES_MIN;
        boolean controlsPassed = controlsCorrect >= CONTROLS_MIN;
        boolean overallPassed = signsPassed && rulesPassed && controlsPassed;

        // --- Update UI ---
        // Overall Result
        overallResultMessage.setText(overallPassed ? "Congratulations! You Passed." : "Sorry, you did not pass. Try again.");
        int totalCorrect = signsCorrect + rulesCorrect + controlsCorrect;
        int totalQuestions = SIGNS_TOTAL + RULES_TOTAL + CONTROLS_TOTAL;
        int percentage = (int) Math.round(((double) totalCorrect / totalQuestions) * 100);
        percentageScoreLabel.setText(String.format("Overall Score: %d%%", percentage));

        // Signs Card
        updateMetricCard(signsCard, signsStatusIcon, signsScoreLabel, signsMinLabel, signsCorrect, SIGNS_TOTAL, SIGNS_MIN, signsPassed);

        // Rules Card
        updateMetricCard(rulesCard, rulesStatusIcon, rulesScoreLabel, rulesMinLabel, rulesCorrect, RULES_TOTAL, RULES_MIN, rulesPassed);

        // Controls Card
        updateMetricCard(controlsCard, controlsStatusIcon, controlsScoreLabel, controlsMinLabel, controlsCorrect, CONTROLS_TOTAL, CONTROLS_MIN, controlsPassed);
    }

    private void updateMetricCard(VBox card, ImageView icon, Label scoreLabel, Label minLabel, int correct, int total, int min, boolean passed) {
        // Only set image if it was successfully loaded
        if ((passed && passIcon != null) || (!passed && failIcon != null)) {
            icon.setImage(passed ? passIcon : failIcon);
        } else {
            // Hide the icon if image is not available
            icon.setVisible(false);
        }
        
        scoreLabel.setText(String.format("You: %d/%d", correct, total));
        minLabel.setText(String.format("Minimum: %d", min));
        String bgColor = passed ? "#c8e6c9" : "#ffcdd2"; // Light green for pass, light red for fail
        card.setStyle(String.format("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: %s; -fx-padding: 10;", bgColor));
    }

    @FXML
    private void handleReviewAllAction() {
        logger.info("'Review All' button clicked.");
        if (screenManager != null && questions != null) {
            ReviewScreen reviewScreen = (ReviewScreen) screenManager.getController("ReviewScreen");
            reviewScreen.setData(questions, ReviewScreen.ReviewMode.POST_SUBMISSION);
            screenManager.setScreen("ReviewScreen");
        }
    }

    @FXML
    private void handleReviewIncorrectAction() {
        logger.info("'Review Incorrect' button clicked.");
        if (screenManager != null && questions != null) {
            ReviewScreen reviewScreen = (ReviewScreen) screenManager.getController("ReviewScreen");
            reviewScreen.setData(questions, ReviewScreen.ReviewMode.POST_SUBMISSION);
            // The logic to filter is on the review screen, which is what we want.
            // The user can click the checkbox themselves.
            screenManager.setScreen("ReviewScreen");
        }
    }

    @FXML
    private void handleDashboardAction() {
        logger.info("'Go to Dashboard' button clicked.");
        if (screenManager != null) {
            screenManager.setScreen("DashboardScreen");
        }
    }

    @Override
    public void setScreenParent(ScreenManager screenParent) {
        this.screenManager = screenParent;
    }

    @Override
    public void runOnScreenChange() {
        // Data is now set via setData, so this can be left empty or used for other purposes.
    }

    @Override
    public void cleanup() {
        // Optional: Clear any state if necessary when the screen is left
        this.questions = null;
    }
}
