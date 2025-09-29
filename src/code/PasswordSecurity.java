package code;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Password Security Utility Class
 * Provides password hashing and verification functionality
 */
public class PasswordSecurity {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * Generate a random salt
     * @return Base64 encoded salt
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hash a password with salt
     * @param password The plain text password
     * @param salt The salt to use
     * @return Hashed password
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            
            // Add salt to password
            String saltedPassword = password + salt;
            
            // Hash the salted password
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            
            // Convert to Base64 string
            return Base64.getEncoder().encodeToString(hashedBytes);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verify a password against a hash
     * @param password The plain text password to verify
     * @param hashedPassword The stored hashed password
     * @param salt The salt used for hashing
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword, String salt) {
        String newHash = hashPassword(password, salt);
        return newHash.equals(hashedPassword);
    }
    
    /**
     * Generate hash with integrated salt (for backward compatibility)
     * Format: hash$salt
     * @param password The plain text password
     * @return Combined hash and salt
     */
    public static String hashPasswordWithSalt(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return hash + "$" + salt;
    }
    
    /**
     * Verify password against combined hash$salt
     * @param password The plain text password
     * @param storedHash The stored hash$salt combination
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPasswordWithSalt(String password, String storedHash) {
        try {
            String[] parts = storedHash.split("\\$");
            if (parts.length != 2) {
                return false;
            }
            
            String hash = parts[0];
            String salt = parts[1];
            
            return verifyPassword(password, hash, salt);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Simple password strength check
     * @param password The password to check
     * @return true if password meets basic requirements
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    /**
     * Get password strength description
     * @param password The password to analyze
     * @return Description of password strength
     */
    public static String getPasswordStrengthDescription(String password) {
        if (password == null || password.isEmpty()) {
            return "Password tidak boleh kosong";
        }
        
        if (password.length() < 8) {
            return "Password minimal 8 karakter";
        }
        
        if (!isPasswordStrong(password)) {
            return "Password harus mengandung huruf besar, kecil, angka, dan simbol";
        }
        
        return "Password kuat";
    }
}