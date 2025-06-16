package leanersdts;

/**
 *
 * @author pitso
 */


import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Deque;
import java.util.LinkedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javafx.scene.layout.VBox;


import javafx.scene.layout.StackPane;
import java.util.Map;

public class ScreenManager extends VBox implements ControlledScreen {

    private static final Logger logger = LoggerFactory.getLogger(ScreenManager.class);
    private final Map<String, Node> screens = new HashMap<>();
    private final Map<String, ControlledScreen> controllers = new HashMap<>();
    private final Deque<String> screenHistory = new LinkedList<>();
    private final StackPane mainContainer;
    

    public ScreenManager(StackPane mainContainer) {
        this.mainContainer = mainContainer;
    }

    public void addScreen(String name, Node screen) {
        logger.info("Adding screen: " + name);
        screens.put(name, screen);
    }

    public Node getScreen(String name) {
        return screens.get(name);
    }

    public ControlledScreen getController(String name) {
        return controllers.get(name);
    }

    public boolean loadScreen(String name, String resource) {
        try {
            logger.info("Loading screen: {} from file: {}", name, resource);
            Node loadScreenNode;
            ControlledScreen myScreenController;

            // Special handling for screens that are fx:root components and load their own FXML
            if ("review".equals(name)) { // Add other fx:root screens here if necessary
                // Assuming ReviewScreen's constructor now loads its FXML and sets itself as root/controller
                ReviewScreen reviewScreenInstance = new ReviewScreen(); 
                loadScreenNode = reviewScreenInstance;       // The instance itself is the Node
                myScreenController = reviewScreenInstance;   // And also the controller
            } else {
                // Standard FXML loading for fx:controller based screens
                FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
                loadScreenNode = myLoader.load();
                myScreenController = myLoader.getController();
                if (myScreenController == null) {
                    // This might happen if the FXML defines an fx:root but we didn't handle it above,
                    // and the root class itself is the controller but wasn't set on the loader.
                    // Or if fx:controller is missing.
                    logger.warn("Controller not found via getController() for screen: {}. Attempting to use root node as controller.", name);
                    if (loadScreenNode instanceof ControlledScreen) {
                        myScreenController = (ControlledScreen) loadScreenNode;
                        logger.info("Successfully used root node as controller for screen: {}", name);
                    } else {
                        logger.error("Failed to obtain controller for screen: {}. Root node is not a ControlledScreen.", name);
                        return false; // Cannot proceed without a controller
                    }
                }
            }

            myScreenController.setScreenParent(this);
            addScreen(name, loadScreenNode);
            controllers.put(name, myScreenController); // Store the controller
            logger.info("Successfully loaded screen: {}", name);
            return true;
        } catch (Exception e) {
            logger.error("Failed to load screen: {}", name, e);
            return false;
        }
    }

    public boolean setScreen(String name) {
        if (screens.get(name) != null) {
            if (!mainContainer.getChildren().isEmpty()) {
                mainContainer.getChildren().remove(0);
                mainContainer.getChildren().add(0, screens.get(name));
            } else {
                mainContainer.getChildren().add(screens.get(name));
            }
            screenHistory.clear();
            screens.keySet().forEach(screenHistory::add);
            logger.info("Screen set successfully: {}", name);
            return true;
        } else {
            logger.error("Screen not found: {}", name);
            //AlertMaker.showErrorMessage("Screen Not Loaded", "The requested screen (" + name + ") has not been loaded.");
            return false;
        }
    }

    @Override
    public void runOnScreenChange() {
        logger.info("Running on screen change");
    }

    @Override
    public void setScreenParent(ScreenManager screenPage) {
        logger.info("Setting screen parent");
    }

    @Override
    public void cleanup() {
        logger.info("Cleaning up ScreenManager");
        screens.clear();
        controllers.clear();
        screenHistory.clear();
    }

    public boolean unloadScreen(String name) {
        if (screens.remove(name) == null) {
            logger.warn("Screen to unload did not exist: {}", name);
            return false;
        } else {
            controllers.remove(name);
            logger.info("Successfully unloaded screen: {}", name);
            return true;
        }
    }

    public void goBack() {
        if (!screenHistory.isEmpty()) {
            String previousScreen = screenHistory.pollLast();
            logger.info("Going back to screen: {}", previousScreen);
            setScreen(previousScreen);
        } else {
            logger.info("No previous screen to go back to");
        }
    }

    
}
