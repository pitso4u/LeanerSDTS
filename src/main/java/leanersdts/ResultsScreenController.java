package leanersdts;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResultsScreenController implements ControlledScreen {

    private static final Logger LOGGER = Logger.getLogger(ResultsScreenController.class.getName());
    
    // Thresholds for passing each category
    private static final int SIGNS_MAX = 28;
    private static final int SIGNS_PASS_THRESHOLD = 24;
    private static final int RULES_MAX = 28;
    private static final int RULES_PASS_THRESHOLD = 24;
    private static final int CONTROLS_MAX = 8;
    private static final int CONTROLS_PASS_THRESHOLD = 6;

    // Overall score and time labels
    @FXML
    private Label scoreLabel;
    @FXML
    private Label timeTakenLabel;
    
    // Learner info labels
    @FXML
    private Label learnerNameLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label scheduleLabel;
    
    // Category score labels
    @FXML
    private Label signsScoreLabel;
    @FXML
    private Label rulesScoreLabel;
    @FXML
    private Label controlsScoreLabel;
    
    // Category status labels and indicators
    @FXML
    private Label signsStatusLabel;
    @FXML
    private Region signsStatusIndicator;
    @FXML
    private Label rulesStatusLabel;
    @FXML
    private Region rulesStatusIndicator;
    @FXML
    private Label controlsStatusLabel;
    @FXML
    private Region controlsStatusIndicator;
    
    // Progress chart
    @FXML
    private BarChart<String, Number> progressChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    
    // Action buttons
    @FXML
    private Button retakeQuizButton;
    @FXML
    private Button reviewAnswersButton;
    @FXML
    private Button exitToDashboardButton;

    private ScreenManager screenManager;
    private List<QuizQuestion> questionsForReview;
    private long timeTakenMillis;
    
    // Category scores
    private int signsScore = 0;
    private int rulesScore = 0;
    private int controlsScore = 0;
    private int signsTotal = 0;
    private int rulesTotal = 0;
    private int controlsTotal = 0;

    @Override
    public void setScreenParent(ScreenManager screenParent) {
        this.screenManager = screenParent;
    }
    
    @FXML
    public void initialize() {
        // Initialize chart
        progressChart.setAnimated(false);
        progressChart.setTitle("Test Performance History");
    }

    public void setData(int score, int totalQuestions, long timeMillis, List<QuizQuestion> questions) {
        this.questionsForReview = questions;
        this.timeTakenMillis = timeMillis;

        // Set learner info
        setLearnerInfo();
        
        // Set overall score
        double percentage = totalQuestions > 0 ? ((double) score / totalQuestions) * 100 : 0;
        scoreLabel.setText(String.format("Your Score: %d/%d (%.2f%%)", score, totalQuestions, percentage));

        // Set time taken
        String timeFormatted = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeMillis),
                TimeUnit.MILLISECONDS.toSeconds(timeMillis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMillis))
        );
        timeTakenLabel.setText("Time Taken: " + timeFormatted);
        
        // Calculate category scores
        calculateCategoryScores(questions);
        
        // Update category UI
        updateCategoryUI();
        
        // Load progress chart
        loadProgressChart();
        
        // Disable review button if there's nothing to review
        if (questionsForReview == null || questionsForReview.isEmpty()) {
            reviewAnswersButton.setDisable(true);
        }
        
        // Save results to server
        saveResultsToServer(score, totalQuestions, questions);
    }
    
    private void setLearnerInfo() {
        // Set learner name from LoginData
        LoginData loginData = LoginData.getInstance();
        if (loginData != null && loginData.getFullName() != null) {
            learnerNameLabel.setText("Learner: " + loginData.getFullName());
        } else {
            learnerNameLabel.setText("Learner: Unknown");
        }
        
        // Set current date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        dateLabel.setText("Date: " + today.format(formatter));
        
        // Fetch schedule from server
        fetchLearnerSchedule();
    }
    
    private void fetchLearnerSchedule() {
        scheduleLabel.setText("Next Scheduled Test: Fetching...");
        
        // Fetch schedule from server in a background thread
        new Thread(() -> {
            try {
                // Get next test date from server
                String nextTestDate = ServerConnector.getLearnerNextSchedule();
                
                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    if (nextTestDate != null && !nextTestDate.isEmpty()) {
                        scheduleLabel.setText("Next Scheduled Test: " + nextTestDate);
                    } else {
                        scheduleLabel.setText("Next Scheduled Test: Not scheduled");
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error fetching learner schedule", e);
                javafx.application.Platform.runLater(() -> {
                    scheduleLabel.setText("Next Scheduled Test: Not available");
                });
            }
        }).start();
    }
    
    private void calculateCategoryScores(List<QuizQuestion> questions) {
        // Reset counters
        signsScore = 0;
        rulesScore = 0;
        controlsScore = 0;
        signsTotal = 0;
        rulesTotal = 0;
        controlsTotal = 0;
        
        // Count questions and correct answers by category
        for (QuizQuestion question : questions) {
            String category = question.getCategory().toLowerCase();
            switch (category) {
                case "signs":
                    signsTotal++;
                    if (question.isCorrect()) signsScore++;
                    break;
                case "rules":
                    rulesTotal++;
                    if (question.isCorrect()) rulesScore++;
                    break;
                case "controls":
                    controlsTotal++;
                    if (question.isCorrect()) controlsScore++;
                    break;
                default:
                    LOGGER.warning("Unknown question category: " + category);
            }
        }
        
        LOGGER.info(String.format("Category scores calculated - Signs: %d/%d, Rules: %d/%d, Controls: %d/%d",
                signsScore, signsTotal, rulesScore, rulesTotal, controlsScore, controlsTotal));
    }
    
    private void updateCategoryUI() {
        // Update Signs category
        signsScoreLabel.setText(signsScore + "/" + (signsTotal > 0 ? signsTotal : SIGNS_MAX));
        boolean signsPassed = signsScore >= SIGNS_PASS_THRESHOLD;
        updateCategoryStatus(signsStatusLabel, signsStatusIndicator, signsPassed);
        
        // Update Rules category
        rulesScoreLabel.setText(rulesScore + "/" + (rulesTotal > 0 ? rulesTotal : RULES_MAX));
        boolean rulesPassed = rulesScore >= RULES_PASS_THRESHOLD;
        updateCategoryStatus(rulesStatusLabel, rulesStatusIndicator, rulesPassed);
        
        // Update Controls category
        controlsScoreLabel.setText(controlsScore + "/" + (controlsTotal > 0 ? controlsTotal : CONTROLS_MAX));
        boolean controlsPassed = controlsScore >= CONTROLS_PASS_THRESHOLD;
        updateCategoryStatus(controlsStatusLabel, controlsStatusIndicator, controlsPassed);
    }
    
    private void updateCategoryStatus(Label statusLabel, Region statusIndicator, boolean passed) {
        if (passed) {
            statusLabel.setText("Passed");
            statusIndicator.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 7.5;"); // Green
        } else {
            statusLabel.setText("Not Passed");
            statusIndicator.setStyle("-fx-background-color: #f44336; -fx-background-radius: 7.5;"); // Red
        }
    }
    
    private void loadProgressChart() {
        progressChart.getData().clear();
        
        // Create series for each category
        XYChart.Series<String, Number> signsSeries = new XYChart.Series<>();
        signsSeries.setName("Signs");
        
        XYChart.Series<String, Number> rulesSeries = new XYChart.Series<>();
        rulesSeries.setName("Rules");
        
        XYChart.Series<String, Number> controlsSeries = new XYChart.Series<>();
        controlsSeries.setName("Controls");
        
        // Add current test results
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd"));
        
        // Calculate percentages for current test
        double signsPercent = signsTotal > 0 ? (double) signsScore / signsTotal * 100 : 0;
        double rulesPercent = rulesTotal > 0 ? (double) rulesScore / rulesTotal * 100 : 0;
        double controlsPercent = controlsTotal > 0 ? (double) controlsScore / controlsTotal * 100 : 0;
        
        signsSeries.getData().add(new XYChart.Data<>(today, signsPercent));
        rulesSeries.getData().add(new XYChart.Data<>(today, rulesPercent));
        controlsSeries.getData().add(new XYChart.Data<>(today, controlsPercent));
        
        // Fetch test history from server in background thread to avoid UI freezing
        new Thread(() -> {
            try {
                List<ProgressTracking> history = ServerConnector.getTestHistory();
                
                // Process history data and update chart on JavaFX thread
                if (history != null && !history.isEmpty()) {
                    // Sort history by date (oldest first)
                    history.sort((a, b) -> {
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate dateA = LocalDate.parse(a.getDate(), formatter);
                            LocalDate dateB = LocalDate.parse(b.getDate(), formatter);
                            return dateA.compareTo(dateB);
                        } catch (Exception e) {
                            return 0; // Keep original order if date parsing fails
                        }
                    });
                    
                    // Take only the last 5 entries (or fewer if less available)
                    int startIndex = Math.max(0, history.size() - 5);
                    List<ProgressTracking> recentHistory = history.subList(startIndex, history.size());
                    
                    // Add historical data points to chart series
                    javafx.application.Platform.runLater(() -> {
                        for (ProgressTracking entry : recentHistory) {
                            try {
                                // Format date as MM/dd for display
                                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd");
                                LocalDate date = LocalDate.parse(entry.getDate(), inputFormatter);
                                String formattedDate = date.format(outputFormatter);
                                
                                // Skip today's date as we already added current test results
                                if (formattedDate.equals(today)) continue;
                                
                                // Calculate percentages
                                double signsHistPercent = entry.getSignsTotal() > 0 ? 
                                    (double) entry.getSignsCorrect() / entry.getSignsTotal() * 100 : 0;
                                    
                                double rulesHistPercent = entry.getRulesTotal() > 0 ? 
                                    (double) entry.getRulesCorrect() / entry.getRulesTotal() * 100 : 0;
                                    
                                double controlsHistPercent = entry.getControlsTotal() > 0 ? 
                                    (double) entry.getControlsCorrect() / entry.getControlsTotal() * 100 : 0;
                                
                                // Add data points
                                signsSeries.getData().add(new XYChart.Data<>(formattedDate, signsHistPercent));
                                rulesSeries.getData().add(new XYChart.Data<>(formattedDate, rulesHistPercent));
                                controlsSeries.getData().add(new XYChart.Data<>(formattedDate, controlsHistPercent));
                            } catch (Exception e) {
                                LOGGER.log(Level.WARNING, "Error processing history entry", e);
                            }
                        }
                    });
                } else {
                    // If no history available, add some sample data for demonstration
                    javafx.application.Platform.runLater(() -> {
                        // Add sample historical data points
                        signsSeries.getData().add(new XYChart.Data<>("06/25", 75.0));
                        rulesSeries.getData().add(new XYChart.Data<>("06/25", 82.0));
                        controlsSeries.getData().add(new XYChart.Data<>("06/25", 62.5));
                        
                        signsSeries.getData().add(new XYChart.Data<>("06/15", 68.0));
                        rulesSeries.getData().add(new XYChart.Data<>("06/15", 71.0));
                        controlsSeries.getData().add(new XYChart.Data<>("06/15", 50.0));
                    });
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error fetching test history", e);
            }
        }).start();
        
        // Add the series to the chart
        progressChart.getData().addAll(signsSeries, rulesSeries, controlsSeries);
    }
    
    private void saveResultsToServer(int score, int totalQuestions, List<QuizQuestion> questions) {
        // Save detailed results to the server including category breakdowns
        new Thread(() -> {
            try {
                // Use the new detailed quiz results method that includes category breakdowns
                boolean saved = ServerConnector.saveDetailedQuizResults(questions, timeTakenMillis);
                
                if (!saved) {
                    // Fall back to the basic method if detailed save fails
                    LOGGER.warning("Failed to save detailed quiz results, trying basic save method");
                    boolean basicSaved = ServerConnector.saveQuizResults(questions, timeTakenMillis);
                    
                    if (!basicSaved) {
                        LOGGER.warning("Failed to save quiz results to server using both methods");
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error saving quiz results", e);
            }
        }).start();
    }

    @FXML
    private void handleRetakeQuizAction(ActionEvent event) {
        if (screenManager != null) {
            // Reset and navigate to TakeQuizScreen
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
                    // Call setDataForPostResults for read-only, graded review mode
                    reviewController.setDataForPostResults(questionsForReview, this.timeTakenMillis);
                    screenManager.setScreen(LeanerSDTS.ReviewScreenID);
                } else {
                    LOGGER.log(Level.SEVERE, "Could not retrieve ReviewScreenController.");
                    AlertMaker.showErrorMessage("Error", "Could not load review screen.");
                }
            } else {
                LOGGER.log(Level.SEVERE, "Could not load ReviewScreen.");
                AlertMaker.showErrorMessage("Error", "Could not load review screen.");
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
        // Logic to run when this screen becomes active
        if (reviewAnswersButton != null && questionsForReview != null && !questionsForReview.isEmpty()) {
            reviewAnswersButton.setDisable(false);
        }
    }

    @Override
    public void cleanup() {
        // Cleanup resources when screen is changed
        this.questionsForReview = null;
        if (progressChart != null) {
            progressChart.getData().clear();
        }
    }
}
