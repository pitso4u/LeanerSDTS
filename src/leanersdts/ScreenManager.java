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

public class ScreenManager extends VBox implements ControlledScreen {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenManager.class);
    private HashMap<String, Node> screens = new HashMap<>();
    private HashMap<String, ControlledScreen> controllers = new HashMap<>();
    private Deque<String> screenHistory = new LinkedList<>();
    private String currentScreen;  // Track the current screen

    public ScreenManager() {
        super();
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

    @Override
    public void runOnScreenChange() {
        // Any logic you want to run when the screen changes
    }

  public void loadScreen(String name, String resource) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent screen = loader.load();

        // Check the type of the root element and cast accordingly
        if (screen instanceof AnchorPane) {
            screens.put(name, (AnchorPane) screen);
        } else if (screen instanceof VBox) {
            screens.put(name, (VBox) screen);
        }

        ControlledScreen controller = loader.getController();
        controller.setScreenParent(this);

        // Add the controller to the controllers map
        controllers.put(name, controller);

    } catch (IOException e) {
        e.printStackTrace();
    }
}
    public boolean setScreen(final String name) {
        // Save the current screen to history
        if (currentScreen != null) {
            screenHistory.add(currentScreen);
        }

        if (screens.containsKey(name)) {
            // Clear existing screens
            getChildren().clear();
            // Add the new screen
            getChildren().add(screens.get(name));
            // Update the current screen
            currentScreen = name;
            return true;
        } else {
            LOGGER.error("Screen hasn't been loaded!");
            AlertMaker.showErrorMessage("Screen Not Loaded", "The requested screen has not been loaded.");
            return false;
        }
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




    @Override
    public void setScreenParent(ScreenManager screenPage) {
    }

    @Override
    public void cleanup() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static class AnimateFXInterpolator {

        public static final Interpolator EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
    }
}
