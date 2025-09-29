package code;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Database Manager Class for secure database connections
 * Implements singleton pattern and proper connection management
 */
public class DatabaseManager {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost/rental_cosplay";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";
    
    private static DatabaseManager instance;
    private String url;
    private String username;
    private String password;
    
    private DatabaseManager() {
        loadDatabaseConfig();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private void loadDatabaseConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/config/database.properties")) {
            if (input != null) {
                props.load(input);
                this.url = props.getProperty("db.url", DEFAULT_URL);
                this.username = props.getProperty("db.username", DEFAULT_USER);
                this.password = props.getProperty("db.password", DEFAULT_PASSWORD);
            } else {
                // Fallback to default values
                this.url = DEFAULT_URL;
                this.username = DEFAULT_USER;
                this.password = DEFAULT_PASSWORD;
            }
        } catch (IOException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            // Use default values
            this.url = DEFAULT_URL;
            this.username = DEFAULT_USER;
            this.password = DEFAULT_PASSWORD;
        }
    }
    
    /**
     * Get a database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            // Ensure MySQL driver is loaded
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    /**
     * Test database connection
     * @return true if connection successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Safely close database resources
     */
    public static void closeResources(Connection conn, java.sql.Statement stmt, java.sql.ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing Statement: " + e.getMessage());
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }
    }
}