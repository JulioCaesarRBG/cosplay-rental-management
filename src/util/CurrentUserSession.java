package util;

import model.User;

/**
 * Current User Session Manager
 * Manages the currently logged-in user session
 */
public class CurrentUserSession {
    
    private static User currentUser;
    private static long sessionStartTime;
    
    // Private constructor to prevent instantiation
    private CurrentUserSession() {}
    
    /**
     * Set the current logged-in user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
        sessionStartTime = System.currentTimeMillis();
        AppLogger.logInfo("User session started for: %s", user != null ? user.getUsername() : "null");
    }
    
    /**
     * Get the current logged-in user
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user is currently logged in
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Get current username
     */
    public static String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "UNKNOWN";
    }
    
    /**
     * Get current user ID
     */
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }
    
    /**
     * Check if current user has specific role
     */
    public static boolean hasRole(User.UserRole requiredRole) {
        return currentUser != null && currentUser.hasRole(requiredRole);
    }
    
    /**
     * Check if session is valid (not expired)
     */
    public static boolean isSessionValid() {
        if (currentUser == null) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        long sessionDuration = (currentTime - sessionStartTime) / 1000; // in seconds
        
        return sessionDuration < AppConstants.System.SESSION_TIMEOUT;
    }
    
    /**
     * Refresh session (update last activity time)
     */
    public static void refreshSession() {
        if (currentUser != null) {
            sessionStartTime = System.currentTimeMillis();
        }
    }
    
    /**
     * Get session duration in seconds
     */
    public static long getSessionDuration() {
        if (sessionStartTime > 0) {
            return (System.currentTimeMillis() - sessionStartTime) / 1000;
        }
        return 0;
    }
    
    /**
     * Clear current session (logout)
     */
    public static void clearSession() {
        if (currentUser != null) {
            String username = currentUser.getUsername();
            AppLogger.logInfo("User session ended for: %s (Duration: %d seconds)", 
                            username, getSessionDuration());
            
            currentUser = null;
            sessionStartTime = 0;
        }
    }
    
    /**
     * Validate session and throw exception if invalid
     */
    public static void validateSession() throws SessionExpiredException {
        if (!isLoggedIn()) {
            throw new SessionExpiredException("No active user session");
        }
        
        if (!isSessionValid()) {
            clearSession();
            throw new SessionExpiredException("Session has expired");
        }
        
        // Refresh session on validation
        refreshSession();
    }
    
    /**
     * Custom exception for session validation
     */
    public static class SessionExpiredException extends Exception {
        public SessionExpiredException(String message) {
            super(message);
        }
    }
}