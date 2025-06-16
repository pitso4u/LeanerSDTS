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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import java.util.logging.Logger;
import java.util.logging.Level;


public class VehicleMaintenanceModuleScreen implements ControlledScreen {
    private static final Logger LOGGER = Logger.getLogger(VehicleMaintenanceModuleScreen.class.getName());

    private ScreenManager screenManager;

    @FXML
    private Button maintenanceTask1Button;

    @FXML
    private Button maintenanceTask2Button;

    // Add more maintenance task buttons as needed

    public void initialize() {
        // Initialization code, if needed
    }

    @FXML
    private void handleMaintenanceTask1ButtonAction() {
        // Add logic for maintenance task 1
        // For example, display details for Maintenance exterior Task 1
        // You can also navigate to a MaintenanceTaskDetailsScreen
        showMaintenanceTaskDetails("Maintenance Exterior Task 1", "Description of how and what to do for Task 1");
    }

    @FXML
    private void handleMaintenanceTask2ButtonAction() {
        // Add logic for maintenance task 2
        // For example, display details for Maintenance interior Task 2
        // You can also navigate to a MaintenanceTaskDetailsScreen
        showMaintenanceTaskDetails("Maintenance Interior Task 2", "Description of how and what to do for Task 2");
    }

    

    // Add more methods for other maintenance tasks as needed

    private void showMaintenanceTaskDetails(String taskName, String taskDescription) {
        // Create a new dialog
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Maintenance Task Details");
        alert.setHeaderText(taskName);

        // Create a VBox to hold the task details
        VBox vbox = new VBox();

        // Add a label for the task description
        Label descriptionLabel = new Label(taskDescription);

        // Add more labels or UI elements as needed

        vbox.getChildren().addAll(descriptionLabel);

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

