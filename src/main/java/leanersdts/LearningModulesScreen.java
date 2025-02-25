/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.leanersdts;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class LearningModulesScreen implements ControlledScreen {

    private ScreenManager screenManager;

    @FXML
    private TextArea articlesTextArea;

    public void initialize() {
        // Set the articles text in the TextArea during initialization
        String lawsOnDriving = "Law 1: Follow Traffic Rules and Regulations"
                + "Always adhere to traffic rules and regulations. "
                + "Obey traffic signs, signals, and laws. "
                + "This ensures your safety and the safety of others on the road."
                + "Law 2: Drive Defensively Practice defensive driving by staying alert, "
                + "anticipating the actions of other drivers, "
                + "and being prepared to react to unexpected situations. This helps prevent accidents.                "
                + "Law 3: Avoid Distractions                "
                + "Avoid distractions such as using a mobile phone, "
                + "eating, or adjusting the radio while driving. "
                + "Focus your attention on the road to reduce the risk of accidents.";

        articlesTextArea.setText(lawsOnDriving);
    }

    // You can add more methods if needed
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
