package util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

/**
 * Application Logger utility class
 * Provides centralized logging functionality for the entire application
 */
public class AppLogger {
    
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE_PATTERN = LOG_DIR + "/rental_cosplay_%g.log";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static Logger applicationLogger;
    private static Logger securityLogger;
    private static Logger databaseLogger;
    private static Logger userActionLogger;
    
    static {
        setupLoggers();
    }
    
    /**
     * Setup all loggers with appropriate handlers and formatters
     */
    private static void setupLoggers() {
        try {
            // Create logs directory if it doesn't exist
            java.io.File logDir = new java.io.File(LOG_DIR);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            // Application Logger
            applicationLogger = Logger.getLogger("RentalCosplay.Application");
            setupLogger(applicationLogger, "logs/application_%g.log");
            
            // Security Logger
            securityLogger = Logger.getLogger("RentalCosplay.Security");
            setupLogger(securityLogger, "logs/security_%g.log");
            
            // Database Logger
            databaseLogger = Logger.getLogger("RentalCosplay.Database");
            setupLogger(databaseLogger, "logs/database_%g.log");
            
            // User Action Logger
            userActionLogger = Logger.getLogger("RentalCosplay.UserAction");
            setupLogger(userActionLogger, "logs/user_actions_%g.log");
            
        } catch (Exception e) {
            System.err.println("Failed to setup loggers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Setup individual logger with file handler
     */
    private static void setupLogger(Logger logger, String logFile) throws IOException {
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        
        // File Handler
        FileHandler fileHandler = new FileHandler(logFile, 10 * 1024 * 1024, 5, true); // 10MB per file, 5 files
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new CustomFormatter());
        logger.addHandler(fileHandler);
        
        // Console Handler for development
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(consoleHandler);
    }
    
    // Application Logging Methods
    public static void logInfo(String message) {
        applicationLogger.info(message);
    }
    
    public static void logInfo(String message, Object... params) {
        applicationLogger.info(String.format(message, params));
    }
    
    public static void logWarning(String message) {
        applicationLogger.warning(message);
    }
    
    public static void logWarning(String message, Object... params) {
        applicationLogger.warning(String.format(message, params));
    }
    
    public static void logError(String message) {
        applicationLogger.severe(message);
    }
    
    public static void logError(String message, Throwable throwable) {
        applicationLogger.log(Level.SEVERE, message, throwable);
    }
    
    public static void logError(String message, Throwable throwable, Object... params) {
        applicationLogger.log(Level.SEVERE, String.format(message, params), throwable);
    }
    
    public static void logDebug(String message) {
        applicationLogger.fine(message);
    }
    
    public static void logDebug(String message, Object... params) {
        applicationLogger.fine(String.format(message, params));
    }
    
    // Security Logging Methods
    public static void logSecurityEvent(String event, String username, String details) {
        String message = String.format("[%s] User: %s - %s", event, username != null ? username : "UNKNOWN", details);
        securityLogger.info(message);
    }
    
    public static void logLoginAttempt(String username, boolean successful, String ipAddress) {
        String status = successful ? "SUCCESS" : "FAILED";
        logSecurityEvent("LOGIN_ATTEMPT", username, String.format("Status: %s, IP: %s", status, ipAddress));
    }
    
    public static void logLogout(String username) {
        logSecurityEvent("LOGOUT", username, "User logged out");
    }
    
    public static void logSecurityViolation(String username, String violation, String details) {
        String message = String.format("[SECURITY_VIOLATION] User: %s - %s: %s", 
                                      username != null ? username : "UNKNOWN", violation, details);
        securityLogger.warning(message);
    }
    
    // Database Logging Methods
    public static void logDatabaseOperation(String operation, String table, boolean successful) {
        String status = successful ? "SUCCESS" : "FAILED";
        String message = String.format("DB Operation: %s on %s - Status: %s", operation, table, status);
        databaseLogger.info(message);
    }
    
    public static void logDatabaseError(String operation, String table, Throwable error) {
        String message = String.format("DB Error during %s on %s", operation, table);
        databaseLogger.log(Level.SEVERE, message, error);
    }
    
    public static void logDatabaseConnection(boolean successful, String details) {
        String status = successful ? "SUCCESS" : "FAILED";
        String message = String.format("DB Connection: %s - %s", status, details);
        if (successful) {
            databaseLogger.info(message);
        } else {
            databaseLogger.warning(message);
        }
    }
    
    // User Action Logging Methods
    public static void logUserAction(String username, String action, String entityType, String entityId, String details) {
        String message = String.format("User: %s - Action: %s on %s[%s] - %s", 
                                      username, action, entityType, entityId, details);
        userActionLogger.info(message);
    }
    
    public static void logUserAction(String username, String action, String details) {
        String message = String.format("User: %s - Action: %s - %s", username, action, details);
        userActionLogger.info(message);
    }
    
    // Business Logic Logging
    public static void logBusinessEvent(String event, String details) {
        String message = String.format("Business Event: %s - %s", event, details);
        applicationLogger.info(message);
    }
    
    public static void logPerformance(String operation, long durationMs) {
        if (durationMs > 1000) { // Log slow operations (>1 second)
            String message = String.format("Performance: %s took %d ms", operation, durationMs);
            applicationLogger.warning(message);
        }
    }
    
    // System Events
    public static void logSystemEvent(String event, String details) {
        String message = String.format("System Event: %s - %s", event, details);
        applicationLogger.info(message);
    }
    
    public static void logApplicationStart() {
        logSystemEvent("APPLICATION_START", "Rental Cosplay application started");
    }
    
    public static void logApplicationStop() {
        logSystemEvent("APPLICATION_STOP", "Rental Cosplay application stopped");
    }
    
    /**
     * Custom formatter for log messages
     */
    private static class CustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return String.format("%s [%s] %s.%s: %s%n",
                    LocalDateTime.now().format(DATE_FORMATTER),
                    record.getLevel(),
                    record.getLoggerName(),
                    record.getSourceMethodName(),
                    record.getMessage());
        }
    }
    
    /**
     * Get logger for specific class
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }
    
    /**
     * Shutdown all loggers properly
     */
    public static void shutdown() {
        Handler[] handlers = applicationLogger.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
        }
        
        handlers = securityLogger.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
        }
        
        handlers = databaseLogger.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
        }
        
        handlers = userActionLogger.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
        }
        
        logApplicationStop();
    }
}