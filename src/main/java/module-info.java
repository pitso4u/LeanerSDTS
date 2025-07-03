module leanersdts {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    // requires javafx.web;
    // requires javafx.media;

    // Third-party libraries
    requires org.slf4j;
    requires org.json;

    // Standard Java modules
    requires java.sql;
    requires java.logging;
    requires java.net.http;

    // Open the package to JavaFX for reflection
    opens leanersdts to javafx.fxml, javafx.graphics;
}
