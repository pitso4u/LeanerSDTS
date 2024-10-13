/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leanersdts;

/**
 *
 * @author pitso
 */
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class DashboardScreen implements ControlledScreen {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardScreen.class);
    private ScreenManager screenManager;

    @FXML
    private Button learningModulesButton;

    @FXML
    private Button roadRulesModuleButton;

    @FXML
    private Button vehicleMaintenanceModuleButton;

    @FXML
    private Button riskPerceptionModuleButton;

    @FXML
    private Button takeQuizButton;

    @FXML
    private Button exitButton;

    private TakeQuizScreen takeQuizScreen;

    public void initialize() {
        // Initialization code, if needed
        learningModulesButton.setStyle("#flatbee-large");
    }
public void setTakeQuizScreen(TakeQuizScreen takeQuizScreen) {
        this.takeQuizScreen = takeQuizScreen;
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
        // Assuming you want to display K53 information when taking a quiz
        displayK53Information();
        TakeQuizScreen takeQuizScreencontroller = (TakeQuizScreen) screenManager.getController(LeanerSDTS.TakeQuizScreenID);
        // Start the quiz timer using the existing instance
        takeQuizScreencontroller.startQuizTimer();

        // Set the screen using the existing instance
        screenManager.setScreen("TakeQuizScreen");
    }


   private void displayK53Information() {
    // You can display K53 information in a pop-up, label, or a new section
    // Here is a simple example using a dialog:
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("K53 Learners Licence Test - 2023 Information");
    alert.setHeaderText(null);
    alert.setContentText(
            "Total Questions: 100\n" +
            "Test Duration: 60 Minutes\n" +
            "Sections:\n" +
            "- Vehicle Controls: 8 questions\n" +
            "- Road Signs: 30 questions\n" +
            "- Driving and Observation Rules: 30 questions\n" +
            "Passing Marks:\n" +
            "- Vehicle Controls: 6\n" +
            "- Road Signs: 23\n" +
            "- Driving and Observation Rules: 22\n" +
            "Pass Percentages:\n" +
            "- Vehicle Controls: 75%\n" +
            "- Road Signs: 77%\n" +
            "- Driving and Observation Rules: 74%\n" +
            "Official Website: https://www.bakubungdrivingschool.co.za\n" +
            "Learners Licence Classes:\n" +
            "- Class 1 (Motorcycle): 16 Years\n" +
            "- Class 2 (Light Motor Vehicles - Cars): 17 Years\n" +
            "- Class 3 (Heavy Motor Vehicles, Trucks, and Busses): 18 Years\n"
    );
    alert.showAndWait();
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

    @FXML
    private void handleExitButtonAction(ActionEvent event) {
          screenManager.setScreen("LoginScreen");
    }
}