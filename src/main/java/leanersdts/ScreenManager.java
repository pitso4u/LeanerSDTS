package main.java.leanersdts;

/**
 *
 * @author pitso
 */
import java.io.IOException;
import javafx.animation.Interpolator;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Deque;
import java.util.LinkedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager extends VBox implements ControlledScreen {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenManager.class);
    private HashMap<String, Node> screens = new HashMap<>();
    private HashMap<String, ControlledScreen> controllers = new HashMap<>();
    private Deque<String> screenHistory = new LinkedList<>();
    private String currentScreen;
    private Stage primaryStage;

    public ScreenManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Modified loadScreen method to return the controller
    public ControlledScreen loadScreen(String screenID, String screenFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(screenFile));
            Parent root = loader.load();

            ControlledScreen controller = loader.getController();
            if (controller == null) {
                LOGGER.error("Controller for screen '{}' is null. Check FXML file: {}", screenID, screenFile);
                return null;
            }

            controller.setScreenParent(this);
            screens.put(screenID, root);
            controllers.put(screenID, controller);

            return controller;
        } catch (Exception e) {
            LOGGER.error("Failed to load screen '{}' from file '{}'. Cause: {}", screenID, screenFile, e.getMessage(), e);
            return null;
        }
    }

    public void addScreen(String name, Node screen) {
        screens.put(name, screen);
    }

    public Node getScreen(String name) {
        return screens.get(name);
    }

    public ControlledScreen getController(String name) {
        return controllers.get(name);
    }

    public void addController(String name, ControlledScreen controlledScreen) {
        controllers.put(name, controlledScreen);
    }

    public void printScreenMaps() {
        LOGGER.info("Screens map: " + screens.keySet());
        LOGGER.info("Controllers map: " + controllers.keySet());
    }

    public boolean setScreen(final String name) {
        if (currentScreen != null) {
            screenHistory.add(currentScreen);
        }

        Node screenNode = screens.get(name);
        if (screenNode != null) {
            getChildren().clear();
            getChildren().add(screenNode);
            currentScreen = name;
            LOGGER.info("Screen set successfully: " + name);
            return true;
        } else {
            LOGGER.error("Screen not found: " + name);
            AlertMaker.showErrorMessage("Screen Not Loaded", "The requested screen (" + name + ") has not been loaded.");
            return false;
        }
    }

    @Override
    public void runOnScreenChange() {
        // Any logic you want to run when the screen changes
    }

    @Override
    public void setScreenParent(ScreenManager screenPage) {
        // This method is required by the interface but not used for ScreenManager
    }

    @Override
    public void cleanup() {
        // Cleanup logic for ScreenManager, if needed
        screens.clear();
        controllers.clear();
        screenHistory.clear();
    }

    public boolean unloadCurrentScreen() {
        if (currentScreen != null) {
            // Get the controller for the current screen
            ControlledScreen controller = controllers.get(currentScreen);
            if (controller != null) {
                controller.cleanup();
            }

            // Remove the current screen from the screens map
            screens.remove(currentScreen);

            // Clear the current screen
            getChildren().clear();

            // Set current screen to null
            currentScreen = null;

            return true;
        } else {
            LOGGER.error("No current screen to unload!");
            // Handle the error appropriately (e.g., show an error message)
            return false;
        }
    }

    public boolean unloadScreen(String name) {
        ControlledScreen controller = controllers.remove(name);
        if (controller != null) {
            controller.cleanup(); // Add a cleanup method to your ControlledScreen interface
        }

        if (screens.remove(name) == null) {
            LOGGER.error("Screen does not exist!!");
            // Handle the error appropriately (e.g., show an error message)
            return false;
        } else {
            return true;
        }
    }

    public void goBack() {
        if (!screenHistory.isEmpty()) {
            // Remove the current screen from the screen history
            String previousScreen = screenHistory.pollLast();

            // Set the previous screen
            setScreen(previousScreen);

            // Clear the screen history
            screenHistory.clear();

            // Add screens to history again up to the current screen
            screens.keySet().forEach(screenHistory::add);
        } else {
            // Handle the case where there is no previous screen (optional)
        }
    }

    private static class AnimateFXInterpolator {

        public static final Interpolator EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
    }
}
