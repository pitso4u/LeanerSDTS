/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leanersdts;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LeanerSDTS extends Application {

    public static String LoginScreenID = "LoginScreen";
    public static String LoginScreenFile = "LoginScreen.fxml";

    public static String DashboardScreenID = "DashboardScreen";
    public static String DashboardScreenFile = "DashboardScreen.fxml";

    public static String LearningModulesScreenID = "LearningModulesScreen";
    public static String LearningModulesScreenFile = "LearningModulesScreen.fxml";
    public static String RiskPerceptionModuleScreenID = "RiskPerceptionModuleScreen";
    public static String RiskPerceptionModuleScreenFile = "RiskPerceptionModuleScreen.fxml";
    public static String RoadRulesModuleScreenID = "RoadRulesModuleScreen";
    public static String RoadRulesModuleScreenFile = "RoadRulesModuleScreen.fxml";
    public static String VehicleMaintenanceModuleScreenID = "VehicleMaintenanceModuleScreen";
    public static String VehicleMaintenanceModuleScreenFile = "VehicleMaintenanceModuleScreen.fxml";
    public static String TakeQuizScreenID = "TakeQuizScreen";
    public static String TakeQuizScreenFile = "TakeQuizScreen.fxml";
    public static String QuizSummaryScreenID = "QuizSummaryScreen";
    public static String QuizSummaryScreenFile = "QuizSummaryScreen.fxml";

    private ScreenManager mainContainer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainContainer = new ScreenManager();
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
        mainContainer.loadScreen(VehicleMaintenanceModuleScreenID, VehicleMaintenanceModuleScreenFile);
        mainContainer.loadScreen(TakeQuizScreenID, TakeQuizScreenFile);
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
System.out.println("sceneWidth:"+sceneWidth);
        System.out.println("sceneHeight:"+sceneHeight);

        primaryStage.setScene(new Scene(mainContainer, sceneWidth, sceneHeight));
        primaryStage.setTitle("SmartDrive Training Suite");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}




