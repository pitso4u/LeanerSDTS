package leanersdts;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.StackPane;
import java.util.logging.Logger;
import java.util.logging.Level;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;

public class AlertMaker {
    private static final Logger logger = Logger.getLogger(AlertMaker.class.getName());

    public static void showSimpleAlert(String title, String content) {
        logger.info("Showing simple alert - Title: " + title + ", Content: " + content);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showErrorMessage(String title, String content) {
        logger.log(Level.SEVERE, "Showing error message - Title: " + title + ", Content: " + content);
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showErrorMessage(Exception ex) {
        logger.log(Level.SEVERE, "Showing error message for exception", ex);
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error occured");
        alert.setHeaderText("An error occurred");
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        alert.setContentText(sw.toString());
        alert.showAndWait();
    }

    public static void showErrorMessage(Exception ex, String title, String content) {
        logger.log(Level.SEVERE, "Showing error message - Title: " + title + ", Content: " + content, ex);
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error occured");
        alert.setHeaderText(title);
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        alert.setContentText(content + "\n" + sw.toString());
        alert.showAndWait();
    }

    public static void showMaterialDialog(StackPane root, Node nodeToBeBlurred, List<Button> controls, String header, String body) {
        logger.info("Showing material dialog - Header: " + header + ", Body: " + body);
        BoxBlur blur = new BoxBlur(3, 3, 3);
        nodeToBeBlurred.setEffect(blur);
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(header);
        dialog.setHeaderText(body);
        controls.forEach(controlButton -> dialog.getDialogPane().getButtonTypes().add(new javafx.scene.control.ButtonType(controlButton.getText())));
        dialog.setOnHidden(event -> nodeToBeBlurred.setEffect(null));
        dialog.showAndWait();
    }
}
