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
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class RiskPerceptionModuleScreen implements ControlledScreen {

    private ScreenManager screenManager;

    @FXML
    private Button scenario1Button;

    @FXML
    private Button scenario2Button;

    // Add more scenario buttons as needed
    public void initialize() {
        // Initialization code, if needed
    }
@FXML
    private void handleScenario1ButtonAction() {
        // Add logic for scenario 1
        // For example, display details for Scenario 1
        showScenarioDetails("Scenario 1", "Detailed description for Scenario 1.");
        // You can also navigate to a ScenarioDetailsScreen if needed
    }

    @FXML
    private void handleScenario2ButtonAction() {
        // Add logic for scenario 2
        // For example, display details for Scenario 2
        showScenarioDetails("Scenario 2", "Detailed description for Scenario 2.");
        // You can also navigate to a ScenarioDetailsScreen if needed
    }

    // Add more methods for other scenarios as needed

    private void showScenarioDetails(String scenarioName, String scenarioDescription) {
        // Create a new dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Scenario Details");
        alert.setHeaderText(scenarioName);

        // Create a VBox to hold the scenario details
        VBox vbox = new VBox();

        // Add a label for the scenario description
        Label descriptionLabel = new Label("Description:");
        TextArea descriptionTextArea = new TextArea(scenarioDescription);
        descriptionTextArea.setEditable(false);

        // Add more labels or UI elements as needed

        vbox.getChildren().addAll(descriptionLabel, descriptionTextArea);

        alert.getDialogPane().setContent(vbox);

        // Set the dialog to be non-blocking (doesn't block input to other windows)
        alert.initModality(Modality.NONE);
        alert.initStyle(StageStyle.UNDECORATED);

        // Show the dialog
        alert.showAndWait();
    }
    @FXML
    private void handleBackButtonAction() {
        // Navigate back to the previous screen (you might need to keep track of the previous screen)
        screenManager.goBack();
    }
    // Add more methods for other scenarios as needed
@Override
    public void cleanup() {
        // Implement any cleanup operations needed for this screen
    }
    @Override
    public void setScreenParent(ScreenManager screenParent) {
        this.screenManager = screenParent;
    }

    @Override
    public void runOnScreenChange() {
        // Code to run when the screen is changed, if needed
    }
}
