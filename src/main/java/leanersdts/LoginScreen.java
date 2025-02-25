package main.java.leanersdts;

/**
 *
 * @author pitso
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginScreen implements ControlledScreen {
 private QuizSummaryScreen quizSummaryScreen = new QuizSummaryScreen();
    private ScreenManager screenManager;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    public void initialize() {
        // Initialization code, if needed
//        usernameField.setText("new_user");
//        passwordField.setText("new_password");
    }

   @FXML
private void handleLoginButtonAction(ActionEvent event) {
    try {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            showAlert("Please enter both username and password.");
            return;
        }

        // Perform login logic here
        boolean loginSuccessful = validateLogin(username, password);

        if (loginSuccessful) {
            // Switch to the dashboard screen
            DashboardScreen dashboardScreenController = (DashboardScreen) screenManager.getController(LeanerSDTS.DashboardScreenID);
            dashboardScreenController.setLearnerName(getLearnerDataFromDatabase(username, password).getFull_name());
            screenManager.setScreen(LeanerSDTS.DashboardScreenID);
        } else {
            showAlert("Login Failed");
        }

    } catch (Exception e) {
        e.printStackTrace(); // Log the exception for debugging
        showAlert("An error occurred during login. Please try again.");
    }
}

//private void handleLoginButtonAction() {
//    String enteredUsername = usernameField.getText();
//    String enteredPassword = passwordField.getText();
//
//    // You should perform the actual login validation here, checking against your database
//    boolean loginSuccessful = validateLogin(enteredUsername, enteredPassword);
//
//    if (loginSuccessful) {
//        // Switch to the dashboard screen
//        QuizSummaryScreen QuizSummaryScreencontroller = (QuizSummaryScreen) screenManager.getController(LeanerSDTS.QuizSummaryScreenID);
//        // Start the quiz timer using the existing instance
//        QuizSummaryScreencontroller.setLearnerName(getLearnerDataFromDatabase( enteredUsername, enteredPassword).getFull_name());
//        LOGGER.info("Learner Name Retrieved: {}", getLearnerDataFromDatabase( enteredUsername, enteredPassword).getFull_name());
//        screenManager.setScreen("DashboardScreen");
//        //QuizSummaryScreen.setLearnerName(enteredUsername);
//        } else {
//            // Display an error message (you might want to use JavaFX Alert)
//            showAlert("Login Failed", "Invalid username or password.", Alert.AlertType.ERROR);
//        }
//}
private LoginData getLearnerDataFromDatabase(String username, String password) {
    try (Connection connection = DatabaseConnector.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM learners WHERE username = ? AND password = ?");
    ) {
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                int learnerId = resultSet.getInt("learner_id");
                String fullName = resultSet.getString("full_name");
                LocalDate dateOfBirth = resultSet.getDate("date_of_birth").toLocalDate();
                String email = resultSet.getString("email");

                LoginData learnerData = new LoginData(learnerId, username, password, fullName, dateOfBirth, email);
                
                return learnerData;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace(); // Handle the exception appropriately
    }
    return null;
}

private boolean validateLogin(String username, String password) {
    try (Connection connection = DatabaseConnector.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM learners WHERE username = ? AND password = ?")) {
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSet.next(); // If there is at least one row, credentials are valid
        }
    } catch (SQLException e) {
        //LOGGER.error("Database connection error: {}", e.getMessage());
        showAlert("An error occurred while connecting to the database. Please try again.");
        return false;
    }
}

private void showAlert(String title) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.showAndWait();
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
