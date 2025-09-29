package service;

import code.DatabaseManager;
import code.InputValidator;
import code.PasswordSecurity;
import model.User;
import util.AppConstants;
import util.AppLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * User Service - handles all user-related business logic
 */
public class UserService {
    
    private static UserService instance;
    
    private UserService() {}
    
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    /**
     * Authenticate user login
     * @param username The username
     * @param password The plain text password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticate(String username, String password) {
        AppLogger.logDebug("Attempting authentication for user: %s", username);
        
        // Input validation
        if (!InputValidator.isNotEmpty(username) || !InputValidator.isNotEmpty(password)) {
            AppLogger.logSecurityViolation(username, "EMPTY_CREDENTIALS", "Username or password is empty");
            return null;
        }
        
        // Sanitize input
        username = InputValidator.sanitizeInput(username);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            // Query user with username
            String query = "SELECT user_id, username, password_hash, salt, email, full_name, role, is_active FROM " +
                          AppConstants.Database.TABLE_USER + " WHERE username = ? AND is_active = 1";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedPasswordHash = rs.getString("password_hash");
                String salt = rs.getString("salt");
                
                // For backward compatibility, check if we have hashed password
                boolean authenticated = false;
                if (storedPasswordHash != null && salt != null) {
                    // Use secure password verification
                    authenticated = PasswordSecurity.verifyPassword(password, storedPasswordHash, salt);
                } else {
                    // Fallback to plain text comparison (for existing data)
                    String plainPassword = rs.getString("password"); // Assuming old column name
                    authenticated = password.equals(plainPassword);
                    
                    // TODO: Migrate password to hashed format
                    if (authenticated) {
                        AppLogger.logWarning("User %s using plain text password. Consider migration.", username);
                    }
                }
                
                if (authenticated) {
                    // Create User object
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setFullName(rs.getString("full_name"));
                    user.setActive(rs.getBoolean("is_active"));
                    user.updateLastLogin();
                    
                    // Update last login time
                    updateLastLogin(user.getUserId());
                    
                    AppLogger.logLoginAttempt(username, true, "localhost");
                    AppLogger.logUserAction(username, AppConstants.Actions.LOGIN, "User logged in successfully");
                    
                    return user;
                } else {
                    AppLogger.logLoginAttempt(username, false, "localhost");
                    AppLogger.logSecurityViolation(username, "INVALID_PASSWORD", "Invalid password attempt");
                }
            } else {
                AppLogger.logLoginAttempt(username, false, "localhost");
                AppLogger.logSecurityViolation(username, "USER_NOT_FOUND", "Login attempt for non-existent user");
            }
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("authenticate", AppConstants.Database.TABLE_USER, e);
            AppLogger.logError("Database error during authentication for user: %s", e, username);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Create new user account
     * @param username The username
     * @param password The plain text password
     * @param email The email address
     * @param fullName The full name
     * @return User object if creation successful, null otherwise
     */
    public User createUser(String username, String password, String email, String fullName) {
        AppLogger.logDebug("Creating new user: %s", username);
        
        // Input validation
        InputValidator.ValidationResult usernameValidation = validateUsername(username);
        if (!usernameValidation.isValid()) {
            AppLogger.logWarning("Invalid username during user creation: %s", usernameValidation.getMessage());
            return null;
        }
        
        if (!PasswordSecurity.isPasswordStrong(password)) {
            AppLogger.logWarning("Weak password during user creation for: %s", username);
            return null;
        }
        
        if (InputValidator.isNotEmpty(email) && !InputValidator.isValidEmail(email)) {
            AppLogger.logWarning("Invalid email during user creation: %s", email);
            return null;
        }
        
        // Check if username already exists
        if (userExists(username)) {
            AppLogger.logWarning("Attempt to create user with existing username: %s", username);
            return null;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            // Hash password
            String salt = PasswordSecurity.generateSalt();
            String passwordHash = PasswordSecurity.hashPassword(password, salt);
            
            // Insert new user
            String query = "INSERT INTO " + AppConstants.Database.TABLE_USER + 
                          " (username, password_hash, salt, email, full_name, role, is_active, created_at) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, InputValidator.sanitizeInput(username));
            pstmt.setString(2, passwordHash);
            pstmt.setString(3, salt);
            pstmt.setString(4, InputValidator.sanitizeInput(email));
            pstmt.setString(5, InputValidator.sanitizeInput(fullName));
            pstmt.setString(6, User.UserRole.USER.name());
            pstmt.setBoolean(7, true);
            pstmt.setTimestamp(8, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get generated user ID
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    
                    // Create User object
                    User user = new User(username, passwordHash, salt);
                    user.setUserId(userId);
                    user.setEmail(email);
                    user.setFullName(fullName);
                    
                    AppLogger.logUserAction("SYSTEM", AppConstants.Actions.CREATE, "User", String.valueOf(userId), 
                                          String.format("New user created: %s", username));
                    AppLogger.logInfo("New user created successfully: %s", username);
                    
                    return user;
                }
            }
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("createUser", AppConstants.Database.TABLE_USER, e);
            AppLogger.logError("Database error during user creation for: %s", e, username);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Update user's last login time
     */
    private void updateLastLogin(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            String query = "UPDATE " + AppConstants.Database.TABLE_USER + 
                          " SET last_login = ? WHERE user_id = ?";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, userId);
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("updateLastLogin", AppConstants.Database.TABLE_USER, e);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Check if username already exists
     */
    private boolean userExists(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            String query = "SELECT COUNT(*) FROM " + AppConstants.Database.TABLE_USER + " WHERE username = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("userExists", AppConstants.Database.TABLE_USER, e);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, rs);
        }
        
        return false;
    }
    
    /**
     * Validate username according to business rules
     */
    private InputValidator.ValidationResult validateUsername(String username) {
        if (!InputValidator.isNotEmpty(username)) {
            return new InputValidator.ValidationResult(false, "Username tidak boleh kosong");
        }
        
        if (!InputValidator.isValidLength(username, 
                AppConstants.Validation.MIN_USERNAME_LENGTH, 
                AppConstants.Validation.MAX_USERNAME_LENGTH)) {
            return new InputValidator.ValidationResult(false, 
                String.format("Username harus %d-%d karakter", 
                    AppConstants.Validation.MIN_USERNAME_LENGTH,
                    AppConstants.Validation.MAX_USERNAME_LENGTH));
        }
        
        if (!username.matches(AppConstants.Validation.USERNAME_PATTERN)) {
            return new InputValidator.ValidationResult(false, 
                "Username hanya boleh mengandung huruf, angka, underscore, titik, dan strip");
        }
        
        return new InputValidator.ValidationResult(true, "Valid");
    }
    
    /**
     * Change user password
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        AppLogger.logDebug("Changing password for user ID: %d", userId);
        
        // Validate new password
        if (!PasswordSecurity.isPasswordStrong(newPassword)) {
            AppLogger.logWarning("Weak password during password change for user ID: %d", userId);
            return false;
        }
        
        // TODO: Verify old password
        // For now, we'll skip old password verification
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            // Generate new salt and hash
            String salt = PasswordSecurity.generateSalt();
            String passwordHash = PasswordSecurity.hashPassword(newPassword, salt);
            
            String query = "UPDATE " + AppConstants.Database.TABLE_USER + 
                          " SET password_hash = ?, salt = ?, updated_at = ? WHERE user_id = ?";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, passwordHash);
            pstmt.setString(2, salt);
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(4, userId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                AppLogger.logUserAction("USER_ID_" + userId, AppConstants.Actions.UPDATE, "Password", 
                                      String.valueOf(userId), "Password changed successfully");
                return true;
            }
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("changePassword", AppConstants.Database.TABLE_USER, e);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Log user logout
     */
    public void logout(String username) {
        AppLogger.logLogout(username);
        AppLogger.logUserAction(username, AppConstants.Actions.LOGOUT, "User logged out");
    }
}