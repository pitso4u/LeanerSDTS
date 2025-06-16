package leanersdts.utils;

import java.util.logging.Logger;

/**
 * Utility class for logging throughout the application.
 * Provides a consistent way to create and use loggers.
 */
public class LoggingUtils {
    
    /**
     * Creates a logger for the specified class.
     *
     * @param clazz The class to create a logger for
     * @return A logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }

    /**
     * Creates a logger with the specified name.
     *
     * @param name The name for the logger
     * @return A logger instance
     */
    public static Logger getLogger(String name) {
        return Logger.getLogger(name);
    }
} 