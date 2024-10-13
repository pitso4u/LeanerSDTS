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

public class RoadRulesModuleScreen implements ControlledScreen {

    private ScreenManager screenManager;


    @FXML
    private Button rule1Button;

    @FXML
    private Button rule2Button;

    // Add more rule buttons as needed

    public void initialize() {
        // Initialization code, if needed
    }

    @FXML
    private void handleRule1ButtonAction() {
        // Add logic for rule 1
        // For example, display details for Rule 1
        showRuleDetails("Rule 1", "Detailed description for Rule 1.");
        // You can also navigate to a RuleDetailsScreen if needed
    }

    @FXML
    private void handleRule2ButtonAction() {
        // Add logic for rule 2
        // For example, display details for Rule 2
        showRuleDetails("Rule 2", "Detailed description for Rule 2.");
        // You can also navigate to a RuleDetailsScreen if needed
    }


    private void showRuleDetails(String ruleName, String ruleDescription) {
        // Create a new dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rule Details");
        alert.setHeaderText(ruleName);

        // Create a VBox to hold the rule details
        VBox vbox = new VBox();

        // Add a label for the rule description
        Label descriptionLabel = new Label("Description:");
        TextArea descriptionTextArea = new TextArea(ruleDescription);
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

    // Add more methods for other rules as needed
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
