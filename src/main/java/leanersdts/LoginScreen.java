package leanersdts;

/**
 *
 * @author pitso
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginScreen implements ControlledScreen {
    private static final Logger logger = LoggerFactory.getLogger(LoginScreen.class);
    private QuizSummaryScreen quizSummaryScreen = new QuizSummaryScreen();
    private ScreenManager screenManager;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    public void initialize() {
        // Initialization logic can be added here if needed in the future.
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        try {
            String username = usernameField.getText();
            logger.info("Login attempt for user: {}", username);

            if (username == null || username.isEmpty() || passwordField.getText() == null || passwordField.getText().isEmpty()) {
                logger.warn("Login attempt with empty username or password");
                showAlert("Please enter both username and password.");
                return;
            }

            // Delegate login to the server, which returns LoginData on success
            LoginData loginData = ServerConnector.validateLogin(username, passwordField.getText());

            if (loginData != null) {
                logger.info("Successful login for user: {}", username);
                // Switch to the dashboard screen
                DashboardScreen dashboardScreenController = (DashboardScreen) screenManager.getController(LeanerSDTS.DashboardScreenID);
                dashboardScreenController.setLearnerName(loginData.getFullName());
                screenManager.setScreen(LeanerSDTS.DashboardScreenID);
            } else {
                logger.warn("Failed login attempt for user: {}", username);
                showAlert("Login Failed: Invalid username or password.");
            }

        } catch (Exception e) {
            logger.error("Error during login process", e);
            showAlert("An error occurred during login. Please try again.");
        }
    }



    private void showAlert(String title) {
        logger.debug("Showing alert: {}", title);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    @Override
    public void cleanup() {
        logger.debug("Cleaning up LoginScreen");
    }

    @Override
    public void setScreenParent(ScreenManager screenParent) {
        logger.debug("Setting screen parent for LoginScreen");
        this.screenManager = screenParent;
    }

    @Override
    public void runOnScreenChange() {
        logger.debug("Running on screen change for LoginScreen");
    }
}
