package code;

import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 * Input Validation Utility Class
 * Provides comprehensive input validation and sanitization
 */
public class InputValidator {
    
    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+62|62|0)[0-9]{8,13}$"
    );
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z\\s\\-\\.]{2,50}$"
    );
    
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9\\s]{1,100}$"
    );
    
    /**
     * Sanitize input string to prevent XSS and injection
     * @param input The input string
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        return input.trim()
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;")
                   .replace("&", "&amp;");
    }
    
    /**
     * Validate email format
     * @param email The email to validate
     * @return true if valid email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate Indonesian phone number
     * @param phone The phone number to validate
     * @return true if valid phone format
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }
    
    /**
     * Validate name (allows letters, spaces, hyphens, dots)
     * @param name The name to validate
     * @return true if valid name format
     */
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }
    
    /**
     * Validate alphanumeric input
     * @param input The input to validate
     * @return true if valid alphanumeric
     */
    public static boolean isValidAlphanumeric(String input) {
        return input != null && ALPHANUMERIC_PATTERN.matcher(input.trim()).matches();
    }
    
    /**
     * Validate numeric input
     * @param input The input string
     * @return true if valid integer
     */
    public static boolean isValidInteger(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        try {
            Integer.parseInt(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate positive integer
     * @param input The input string
     * @return true if valid positive integer
     */
    public static boolean isValidPositiveInteger(String input) {
        if (!isValidInteger(input)) {
            return false;
        }
        
        try {
            int value = Integer.parseInt(input.trim());
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate decimal number
     * @param input The input string
     * @return true if valid decimal
     */
    public static boolean isValidDecimal(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        try {
            Double.parseDouble(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Check if string is not null and not empty after trimming
     * @param input The input string
     * @return true if not empty
     */
    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }
    
    /**
     * Validate string length
     * @param input The input string
     * @param minLength Minimum length
     * @param maxLength Maximum length
     * @return true if length is within bounds
     */
    public static boolean isValidLength(String input, int minLength, int maxLength) {
        if (input == null) {
            return minLength == 0;
        }
        
        int length = input.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Show validation error message
     * @param parent Parent component for dialog
     * @param message Error message
     */
    public static void showValidationError(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent, 
            message, 
            "Input Validation Error", 
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Comprehensive validation for costume name
     * @param name The costume name
     * @return ValidationResult with status and message
     */
    public static ValidationResult validateCostumeName(String name) {
        if (!isNotEmpty(name)) {
            return new ValidationResult(false, "Nama karakter tidak boleh kosong");
        }
        
        if (!isValidLength(name, 2, 50)) {
            return new ValidationResult(false, "Nama karakter harus 2-50 karakter");
        }
        
        if (!isValidAlphanumeric(name)) {
            return new ValidationResult(false, "Nama karakter hanya boleh mengandung huruf dan angka");
        }
        
        return new ValidationResult(true, "Valid");
    }
    
    /**
     * Comprehensive validation for customer data
     * @param name Customer name
     * @param phone Phone number
     * @param email Email (optional)
     * @return ValidationResult
     */
    public static ValidationResult validateCustomerData(String name, String phone, String email) {
        if (!isValidName(name)) {
            return new ValidationResult(false, "Nama pelanggan tidak valid (2-50 karakter, huruf dan spasi)");
        }
        
        if (!isValidPhoneNumber(phone)) {
            return new ValidationResult(false, "Nomor telepon tidak valid (format Indonesia)");
        }
        
        if (isNotEmpty(email) && !isValidEmail(email)) {
            return new ValidationResult(false, "Format email tidak valid");
        }
        
        return new ValidationResult(true, "Valid");
    }
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
}