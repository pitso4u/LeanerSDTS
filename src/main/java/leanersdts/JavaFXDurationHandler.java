package main.java.leanersdts;

import javafx.util.Duration;

public class JavaFXDurationHandler {

    // Convert seconds to javafx.util.Duration
    public static Duration fromSeconds(double seconds) {
        return Duration.seconds(seconds);
    }

    // Convert javafx.util.Duration to seconds
    public static double toSeconds(Duration duration) {
        return duration.toSeconds();
    }

    // Example method to format Duration as a string (if needed)
    public static String formatDuration(Duration duration) {
        long minutes = (long) duration.toMinutes();
        long seconds = (long) (duration.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
