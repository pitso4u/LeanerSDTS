package leanersdts;

import javafx.application.Application;

import javafx.scene.Scene;


import javafx.stage.Stage;

import javafx.scene.layout.StackPane;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeanerSDTS extends Application implements ServerDiscovery.ServerDiscoveryListener {
    private static final Logger logger = LoggerFactory.getLogger(LeanerSDTS.class);

    public static final String LoginScreenID = "login";
    public static final String LoginScreenFile = "LoginScreen.fxml";

    public static final String DashboardScreenID = "dashboard";
    public static final String DashboardScreenFile = "DashboardScreen.fxml";

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
    // Removed unused QuizScreenID
    // Removed unused QuizScreenFile
    public static final String ReviewScreenID = "review";
    public static final String ReviewScreenFile = "ReviewScreen.fxml";
    public static String QuizSummaryScreenID = "QuizSummaryScreen";
    public static String QuizSummaryScreenFile = "QuizSummaryScreen.fxml";

    public static final String ResultsScreenID = "results";
    public static final String ResultsScreenFile = "ResultsScreen.fxml";

    private ScreenManager screenManager;
    private StackPane mainContainer;
    private ServerDiscovery serverDiscovery;

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting application");
            mainContainer = new StackPane();
            screenManager = new ScreenManager(mainContainer);
            
            // Load the login screen
                        screenManager.loadScreen(LoginScreenID, LoginScreenFile);
            screenManager.loadScreen(DashboardScreenID, DashboardScreenFile);
            screenManager.loadScreen(LearningModulesScreenID, LearningModulesScreenFile);
            screenManager.loadScreen(RiskPerceptionModuleScreenID, RiskPerceptionModuleScreenFile);
            screenManager.loadScreen(RoadRulesModuleScreenID, RoadRulesModuleScreenFile);
            screenManager.loadScreen(VehicleMaintenanceModuleScreenID, VehicleMaintenanceModuleScreenFile);
            logger.info("[LeanerSDTS] Attempting to load TakeQuizScreen FXML.");
            boolean takeQuizLoaded = screenManager.loadScreen(TakeQuizScreenID, TakeQuizScreenFile);
            logger.info("[LeanerSDTS] Finished loading TakeQuizScreen FXML. Success: " + takeQuizLoaded);
            screenManager.loadScreen(ReviewScreenID, ReviewScreenFile); // Added ReviewScreen loading
            screenManager.loadScreen(ResultsScreenID, ResultsScreenFile); // Added ResultsScreen loading
            logger.info("[LeanerSDTS] Setting initial screen to LoginScreen.");
            screenManager.setScreen(LoginScreenID);

            Scene scene = new Scene(mainContainer);
            primaryStage.setTitle("LeanerSDTS");
            primaryStage.setScene(scene);
            primaryStage.show();

            logger.info("[LeanerSDTS] Preparing to start server discovery.");
            // Start server discovery
            serverDiscovery = new ServerDiscovery(this);
            serverDiscovery.startDiscovery();
            logger.info("[LeanerSDTS] Server discovery process initiated.");
            
            logger.info("Application started successfully, discovery initiated.");
        } catch (Exception e) {
            logger.error("Failed to start application", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void onServerDiscovered(String ipAddress, int port) {
        String discoveredBaseUrl = "http://" + ipAddress + ":" + port + "/";
        ServerConnector.setBaseUrl(discoveredBaseUrl);
        logger.info("[LeanerSDTS] onServerDiscovered: ServerConnector.baseUrl set to: " + discoveredBaseUrl);
        logger.info("Server discovered: " + discoveredBaseUrl);
        // Optionally, trigger UI update or enable login button here
    }

    @Override
    public void onDiscoveryError(String message) {
        logger.error("[LeanerSDTS] onDiscoveryError: " + message);
        logger.error("Server discovery error: " + message);
        // Optionally, show an alert to the user
        // e.g., AlertUtils.showError("Server Not Found", "Could not connect to the server. Please ensure the server is running and on the same network.");
    }

    @Override
    public void stop() throws Exception {
        if (serverDiscovery != null) {
            serverDiscovery.stopDiscovery();
        }
        super.stop();
    }
}




