package util;

import java.math.BigDecimal;

/**
 * Application Constants
 * Contains all application-wide constants and configuration values
 */
public final class AppConstants {
    
    // Prevent instantiation
    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // Application Information
    public static final class Application {
        public static final String NAME = "RentCosIjichi - Rental Cosplay Management System";
        public static final String VERSION = "2.0.0";
        public static final String COPYRIGHT = "© 2025 RentCosIjichi. All rights reserved.";
        public static final String DESCRIPTION = "Professional costume rental management system";
    }
    
    // Database Configuration
    public static final class Database {
        public static final String CONFIG_FILE = "/config/database.properties";
        public static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/rental_cosplay";
        public static final String DEFAULT_USER = "root";
        public static final String DEFAULT_PASSWORD = "";
        public static final int CONNECTION_TIMEOUT = 10000; // 10 seconds
        public static final int MAX_POOL_SIZE = 20;
        public static final int MIN_POOL_SIZE = 5;
        
        // Table Names
        public static final String TABLE_USER = "user";
        public static final String TABLE_COSTUME = "kostum";
        public static final String TABLE_CUSTOMER = "pelanggan";
        public static final String TABLE_RENTAL = "rental";
        public static final String TABLE_RETURN = "pengembalian";
    }
    
    // UI Configuration
    public static final class UI {
        public static final String LOGO_PATH = "/Gambar/logo.png";
        public static final String ICON_PATH = "/Gambar/logo.png";
        
        // Colors (RGB values)
        public static final int[] PRIMARY_COLOR = {0, 153, 204}; // Blue
        public static final int[] SECONDARY_COLOR = {250, 248, 248}; // Light Gray
        public static final int[] ACCENT_COLOR = {255, 255, 51}; // Yellow
        public static final int[] SUCCESS_COLOR = {76, 175, 80}; // Green
        public static final int[] ERROR_COLOR = {244, 67, 54}; // Red
        public static final int[] WARNING_COLOR = {255, 193, 7}; // Orange
        
        // Font Sizes
        public static final int HEADER_FONT_SIZE = 18;
        public static final int BODY_FONT_SIZE = 14;
        public static final int SMALL_FONT_SIZE = 12;
        
        // Component Dimensions
        public static final int BUTTON_HEIGHT = 35;
        public static final int TEXT_FIELD_HEIGHT = 25;
        public static final int DEFAULT_PADDING = 10;
        
        // Window Dimensions
        public static final int LOGIN_WIDTH = 455;
        public static final int LOGIN_HEIGHT = 400;
        public static final int MAIN_WINDOW_WIDTH = 1200;
        public static final int MAIN_WINDOW_HEIGHT = 800;
    }
    
    // Business Rules
    public static final class Business {
        // Rental Rules
        public static final int MAX_RENTAL_DAYS = 30;
        public static final int MIN_RENTAL_DAYS = 1;
        public static final int MAX_RENTAL_QUANTITY = 10;
        public static final BigDecimal DAILY_LATE_FEE = new BigDecimal("5000"); // IDR 5,000 per day
        public static final BigDecimal MIN_RENTAL_AMOUNT = new BigDecimal("50000"); // IDR 50,000
        
        // Customer Rules
        public static final int MIN_CUSTOMER_NAME_LENGTH = 2;
        public static final int MAX_CUSTOMER_NAME_LENGTH = 50;
        public static final int MAX_RENTAL_PER_CUSTOMER = 5; // Simultaneous rentals
        
        // Costume Rules
        public static final int MIN_COSTUME_NAME_LENGTH = 2;
        public static final int MAX_COSTUME_NAME_LENGTH = 100;
        public static final BigDecimal MIN_COSTUME_PRICE = new BigDecimal("10000"); // IDR 10,000
        public static final BigDecimal MAX_COSTUME_PRICE = new BigDecimal("1000000"); // IDR 1,000,000
        public static final int MAX_COSTUME_STOCK = 100;
        
        // Discount Rules
        public static final BigDecimal VIP_DISCOUNT_RATE = new BigDecimal("0.10"); // 10%
        public static final BigDecimal BULK_DISCOUNT_THRESHOLD = new BigDecimal("500000"); // IDR 500,000
        public static final BigDecimal BULK_DISCOUNT_RATE = new BigDecimal("0.05"); // 5%
    }
    
    // Validation Rules
    public static final class Validation {
        // Password Rules
        public static final int MIN_PASSWORD_LENGTH = 8;
        public static final int MAX_PASSWORD_LENGTH = 100;
        public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]";
        
        // Username Rules
        public static final int MIN_USERNAME_LENGTH = 3;
        public static final int MAX_USERNAME_LENGTH = 50;
        public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_.-]+$";
        
        // Phone Number Rules
        public static final String PHONE_PATTERN = "^(\\+62|62|0)[0-9]{8,13}$";
        
        // Email Rules
        public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        
        // Name Rules
        public static final String NAME_PATTERN = "^[a-zA-Z\\s\\-\\.]{2,50}$";
        
        // Input Limits
        public static final int MAX_TEXT_LENGTH = 255;
        public static final int MAX_DESCRIPTION_LENGTH = 1000;
        public static final int MAX_NOTES_LENGTH = 500;
    }
    
    // Error Messages
    public static final class ErrorMessages {
        public static final String REQUIRED_FIELD = "Field ini wajib diisi";
        public static final String INVALID_FORMAT = "Format tidak valid";
        public static final String INVALID_LENGTH = "Panjang input tidak valid";
        public static final String INVALID_EMAIL = "Format email tidak valid";
        public static final String INVALID_PHONE = "Format nomor telepon tidak valid";
        public static final String INVALID_PASSWORD = "Password harus minimal 8 karakter dengan kombinasi huruf besar, kecil, angka, dan simbol";
        public static final String PASSWORD_MISMATCH = "Password tidak sama";
        public static final String WEAK_PASSWORD = "Password terlalu lemah";
        
        // Database Errors
        public static final String DB_CONNECTION_FAILED = "Gagal terhubung ke database";
        public static final String DB_OPERATION_FAILED = "Operasi database gagal";
        public static final String DATA_NOT_FOUND = "Data tidak ditemukan";
        public static final String DATA_ALREADY_EXISTS = "Data sudah ada";
        public static final String CONSTRAINT_VIOLATION = "Pelanggaran aturan data";
        
        // Business Logic Errors
        public static final String INSUFFICIENT_STOCK = "Stok tidak mencukupi";
        public static final String INVALID_RENTAL_PERIOD = "Periode rental tidak valid";
        public static final String COSTUME_NOT_AVAILABLE = "Kostum tidak tersedia";
        public static final String CUSTOMER_LIMIT_EXCEEDED = "Batas rental pelanggan terlampaui";
        public static final String RENTAL_OVERDUE = "Rental telah melewati batas waktu";
    }
    
    // Success Messages
    public static final class SuccessMessages {
        public static final String LOGIN_SUCCESS = "Login berhasil";
        public static final String LOGOUT_SUCCESS = "Logout berhasil";
        public static final String DATA_SAVED = "Data berhasil disimpan";
        public static final String DATA_UPDATED = "Data berhasil diperbarui";
        public static final String DATA_DELETED = "Data berhasil dihapus";
        public static final String RENTAL_SUCCESS = "Penyewaan berhasil diproses";
        public static final String RETURN_SUCCESS = "Pengembalian berhasil diproses";
        public static final String PAYMENT_SUCCESS = "Pembayaran berhasil diproses";
    }
    
    // System Configuration
    public static final class System {
        public static final String DATE_FORMAT = "yyyy-MM-dd";
        public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        public static final String TIME_FORMAT = "HH:mm:ss";
        public static final String CURRENCY_FORMAT = "IDR #,##0";
        
        // File Paths
        public static final String CONFIG_DIR = "config";
        public static final String LOG_DIR = "logs";
        public static final String BACKUP_DIR = "backup";
        public static final String REPORTS_DIR = "reports";
        public static final String TEMP_DIR = "temp";
        
        // Security
        public static final int SESSION_TIMEOUT = 3600; // 1 hour in seconds
        public static final int MAX_LOGIN_ATTEMPTS = 5;
        public static final int LOCKOUT_DURATION = 900; // 15 minutes in seconds
        public static final String ENCRYPTION_ALGORITHM = "SHA-256";
    }
    
    // Shipping Methods
    public static final class Shipping {
        public static final String[] METHODS = {
            "Tanpa Ekspedisi",
            "JNE",
            "JNT",
            "SiCepat",
            "Paxel",
            "GoSend",
            "Grab"
        };
        
        // Shipping Costs (in IDR)
        public static final BigDecimal NO_SHIPPING = BigDecimal.ZERO;
        public static final BigDecimal JNE_COST = new BigDecimal("15000");
        public static final BigDecimal JNT_COST = new BigDecimal("12000");
        public static final BigDecimal SICEPAT_COST = new BigDecimal("13000");
        public static final BigDecimal PAXEL_COST = new BigDecimal("20000");
        public static final BigDecimal GOSEND_COST = new BigDecimal("25000");
        public static final BigDecimal GRAB_COST = new BigDecimal("22000");
    }
    
    // Report Types
    public static final class Reports {
        public static final String DAILY_RENTAL = "Daily Rental Report";
        public static final String MONTHLY_RENTAL = "Monthly Rental Report";
        public static final String CUSTOMER_REPORT = "Customer Report";
        public static final String COSTUME_REPORT = "Costume Report";
        public static final String FINANCIAL_REPORT = "Financial Report";
        public static final String OVERDUE_REPORT = "Overdue Report";
    }
    
    // Action Types for Logging
    public static final class Actions {
        public static final String CREATE = "CREATE";
        public static final String READ = "READ";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
        public static final String RENT = "RENT";
        public static final String RETURN = "RETURN";
        public static final String PAYMENT = "PAYMENT";
        public static final String EXPORT = "EXPORT";
        public static final String IMPORT = "IMPORT";
    }
}