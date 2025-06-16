package leanersdts;

import java.time.Duration;
import java.time.Instant;

public class JavaTimeDurationHandler {

    // Calculate the duration between two Instants
    public static Duration between(Instant start, Instant end) {
        return Duration.between(start, end);
    }

    // Convert seconds to java.time.Duration
    public static Duration fromSeconds(long seconds) {
        return Duration.ofSeconds(seconds);
    }

    // Example method to format Duration as a string
    public static String formatDuration(Duration duration) {
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
