package main.java.leanersdts;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DashboardScreen implements ControlledScreen {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardScreen.class);

    private ScreenManager screenManager;

    @FXML
    private Button learningModulesButton, roadRulesModuleButton, vehicleMaintenanceModuleButton,
            riskPerceptionModuleButton, takeQuizButton;

    @FXML
    private Label learnerNameLabel;
    @FXML
    private Button ExitButton;

    public void initialize() {
        // Initialization code if needed
        LOGGER.info("DashboardScreen initialized.");
    }

    @FXML
    private void handleLearningModulesButtonAction() {
        screenManager.setScreen("LearningModulesScreen");
    }

    @FXML
    private void handleRoadRulesModuleButtonAction() {
        screenManager.setScreen("RoadRulesModuleScreen");
    }

    @FXML
    private void handleVehicleMaintenanceModuleButtonAction() {
        screenManager.setScreen("VehicleMaintenanceModuleScreen");
    }

    @FXML
    private void handleRiskPerceptionModuleButtonAction() {
        screenManager.setScreen("RiskPerceptionModuleScreen");
    }

    @FXML
    private void handleTakeQuizButtonAction() {
//        screenManager.setScreen("TakeQuizScreen");
        if (screenManager == null) {
            LOGGER.error("ScreenManager is not initialized.");
            showAlert("Error", "Application error. Please restart the application.");
            return;
        }

        // Load the TakeQuizScreen if not already loaded
        ControlledScreen controller = screenManager.getController(LeanerSDTS.QuizScreenID);
        if (controller == null) {
            LOGGER.info("TakeQuizScreen not loaded. Attempting to load now...");
            controller = screenManager.loadScreen(LeanerSDTS.TakeQuizScreenID, LeanerSDTS.TakeQuizScreenFile);
            if (controller == null) {
                LOGGER.error("Failed to load TakeQuizScreen.");
                showAlert("Error", "Quiz screen is not available. Please contact support.");
                return;
            }
        }

        // Ensure the loaded screen is a TakeQuizScreen instance
        if (controller instanceof TakeQuizScreen) {
            TakeQuizScreen takeQuizScreenController = (TakeQuizScreen) controller;
            // Start the quiz timer
            takeQuizScreenController.startQuizTimer();

            // Switch to the quiz screen
            if (!screenManager.setScreen(LeanerSDTS.TakeQuizScreenID)) {
                LOGGER.error("Failed to switch to TakeQuizScreen.");
                showAlert("Error", "Unable to display the quiz screen. Please try again.");
            }
        } else {
            LOGGER.error("Loaded controller is not a TakeQuizScreen instance.");
            showAlert("Error", "Unexpected screen configuration. Please contact support.");
        }
    }

    private void displayK53Information() {
        // You can display K53 information in a pop-up, label, or a new section
        // Here is a simple example using a dialog:
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("K53 Learners Licence Test - 2023 Information");
        alert.setHeaderText(null);
        alert.setContentText(
                "Total Questions: 100\n"
                + "Test Duration: 60 Minutes\n"
                + "Sections:\n"
                + "- Vehicle Controls: 8 questions\n"
                + "- Road Signs: 30 questions\n"
                + "- Driving and Observation Rules: 30 questions\n"
                + "Passing Marks:\n"
                + "- Vehicle Controls: 6\n"
                + "- Road Signs: 23\n"
                + "- Driving and Observation Rules: 22\n"
                + "Pass Percentages:\n"
                + "- Vehicle Controls: 75%\n"
                + "- Road Signs: 77%\n"
                + "- Driving and Observation Rules: 74%\n"
                + "Official Website: https://www.bakubungdrivingschool.co.za\n"
                + "Learners Licence Classes:\n"
                + "- Class 1 (Motorcycle): 16 Years\n"
                + "- Class 2 (Light Motor Vehicles - Cars): 17 Years\n"
                + "- Class 3 (Heavy Motor Vehicles, Trucks, and Busses): 18 Years\n"
        );
        alert.showAndWait();
    }

    @FXML
    private void handleExitButtonAction(ActionEvent event) {
        screenManager.setScreen("LoginScreen");
    }

    @Override
    public void setScreenParent(ScreenManager screenParent) {
        this.screenManager = screenParent;
    }

    @Override
    public void runOnScreenChange() {
        // Any code to run when this screen is shown
    }

    @Override
    public void cleanup() {
        // Cleanup code, if needed
    }

    public void setLearnerName(String learnerName) {
        if (learnerNameLabel == null) {
            LOGGER.error("Learner name label is not initialized.");
            return;
        }
        learnerNameLabel.setText(learnerName);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
