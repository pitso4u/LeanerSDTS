package main.java.leanersdts;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import main.java.leanersdts.ScreenManager;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class LeanerSDTS extends Application {

    public static String LoginScreenID = "LoginScreen";
    public static String LoginScreenFile = "/main/resources/leanersdts/LoginScreen.fxml";

    public static String DashboardScreenID = "DashboardScreen";
    public static String DashboardScreenFile = "/main/resources/leanersdts/DashboardScreen.fxml";

    public static String LearningModulesScreenID = "LearningModulesScreen";
    public static String LearningModulesScreenFile = "/main/resources/leanersdts/LearningModulesScreen.fxml";
    public static String RiskPerceptionModuleScreenID = "RiskPerceptionModuleScreen";
    public static String RiskPerceptionModuleScreenFile = "/main/resources/leanersdts/RiskPerceptionModuleScreen.fxml";
    public static String RoadRulesModuleScreenID = "RoadRulesModuleScreen";
    public static String RoadRulesModuleScreenFile = "/main/resources/leanersdts/RoadRulesModuleScreen.fxml";
    public static String VehicleMaintenanceModuleScreenID = "VehicleMaintenanceModuleScreen";
    public static String VehicleMaintenanceModuleScreenFile = "/main/resources/leanersdts/VehicleMaintenanceModuleScreen.fxml";
    public static String TakeQuizScreenID = "TakeQuizScreen";
    public static String TakeQuizScreenFile = "/main/resources/leanersdts/TakeQuizScreen.fxml";
    public static String QuizScreenID = "QuizScreen";
    public static String QuizScreenFile = "/main/resources/leanersdts/QuizScreen.fxml";
    public static String ReviewScreenID = "ReviewScreen";
    public static String ReviewScreenFile = "/main/resources/leanersdts/ReviewScreen.fxml";
    public static String QuizSummaryScreenID = "QuizSummaryScreen";
    public static String QuizSummaryScreenFile = "/main/resources/leanersdts/QuizSummaryScreen.fxml";

    private ScreenManager mainContainer;

    @Override
    public void start(Stage primaryStage) {
        try {
            mainContainer = new ScreenManager(primaryStage);
            // Create instances
            //        DashboardScreen dashboard = new DashboardScreen();
            //        TakeQuizScreen takeQuizScreen = new TakeQuizScreen();
            //
            //        // Set the existing TakeQuizScreen instance
            //        dashboard.setTakeQuizScreen(takeQuizScreen);

            // Load other screens

            mainContainer.loadScreen(LoginScreenID, LoginScreenFile);
            mainContainer.loadScreen(DashboardScreenID, DashboardScreenFile);
            mainContainer.loadScreen(LearningModulesScreenID, LearningModulesScreenFile);
            mainContainer.loadScreen(RiskPerceptionModuleScreenID, RiskPerceptionModuleScreenFile);
            mainContainer.loadScreen(RoadRulesModuleScreenID, RoadRulesModuleScreenFile);
            mainContainer.loadScreen(ReviewScreenID, ReviewScreenFile);
            mainContainer.loadScreen(VehicleMaintenanceModuleScreenID, VehicleMaintenanceModuleScreenFile);
            mainContainer.loadScreen(TakeQuizScreenID, TakeQuizScreenFile); // Ensure this line is present
            mainContainer.loadScreen(QuizScreenID, QuizScreenFile); // Ensure this line is present
            mainContainer.loadScreen(QuizSummaryScreenID, QuizSummaryScreenFile);

            // Set the initial screen (e.g., Login screen)
            mainContainer.setScreen(LeanerSDTS.LoginScreenID);

            // Link your CSS file
            // Link your CSS file
            String cssFilePath = getClass().getResource("customstyles.css").toExternalForm();
            mainContainer.getStylesheets().add(cssFilePath);
            // Load the icon
            //        Image icon = new Image(getClass().getResourceAsStream("../Images/OnlyHeadbakubungClear.ico"));

            // Set the icon for the primary stage
            //        primaryStage.getIcons().add(icon);

            // Set the stage to full screen
            primaryStage.setFullScreen(true);
            mainContainer.setStyle("-fx-background-color: #ffed4b;");
            // Get the primary screen
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            // Calculate the desired scene size based on screen resolution
            double screenWidth = bounds.getWidth();
            double screenHeight = bounds.getHeight();
            double sceneWidth = screenWidth * 0.8; // 80% of screen width
            double sceneHeight = screenHeight * 0.8; // 80% of screen height
            System.out.println("sceneWidth:" + sceneWidth);
            System.out.println("sceneHeight:" + sceneHeight);

            primaryStage.setScene(new Scene(mainContainer, sceneWidth, sceneHeight));
            primaryStage.setTitle("SmartDrive Training Suite");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}




