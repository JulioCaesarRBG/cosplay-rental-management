package service;

import code.DatabaseManager;
import code.InputValidator;
import model.Costume;
import util.AppConstants;
import util.AppLogger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Costume Service - handles all costume-related business logic
 */
public class CostumeService {
    
    private static CostumeService instance;
    
    private CostumeService() {}
    
    public static synchronized CostumeService getInstance() {
        if (instance == null) {
            instance = new CostumeService();
        }
        return instance;
    }
    
    /**
     * Get all costumes
     */
    public List<Costume> getAllCostumes() {
        AppLogger.logDebug("Retrieving all costumes");
        
        List<Costume> costumes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            String query = "SELECT IDKostum, NamaKarakter, AsalKarakter, Ukuran, Stok, Harga " +
                          "FROM " + AppConstants.Database.TABLE_COSTUME + " ORDER BY NamaKarakter";
            
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Costume costume = mapResultSetToCostume(rs);
                costumes.add(costume);
            }
            
            AppLogger.logDatabaseOperation("SELECT", AppConstants.Database.TABLE_COSTUME, true);
            AppLogger.logDebug("Retrieved %d costumes", costumes.size());
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("getAllCostumes", AppConstants.Database.TABLE_COSTUME, e);
            AppLogger.logError("Error retrieving costumes", e);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, rs);
        }
        
        return costumes;
    }
    
    /**
     * Get costume by ID
     */
    public Costume getCostumeById(int costumeId) {
        AppLogger.logDebug("Retrieving costume with ID: %d", costumeId);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            String query = "SELECT IDKostum, NamaKarakter, AsalKarakter, Ukuran, Stok, Harga " +
                          "FROM " + AppConstants.Database.TABLE_COSTUME + " WHERE IDKostum = ?";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, costumeId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Costume costume = mapResultSetToCostume(rs);
                AppLogger.logDatabaseOperation("SELECT", AppConstants.Database.TABLE_COSTUME, true);
                return costume;
            } else {
                AppLogger.logWarning("Costume not found with ID: %d", costumeId);
            }
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("getCostumeById", AppConstants.Database.TABLE_COSTUME, e);
            AppLogger.logError("Error retrieving costume with ID: %d", e, costumeId);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Create new costume
     */
    public Costume createCostume(String characterName, String characterOrigin, String size, int stock, BigDecimal price) {
        AppLogger.logDebug("Creating new costume: %s", characterName);
        
        // Input validation
        InputValidator.ValidationResult validation = validateCostumeData(characterName, characterOrigin, size, stock, price);
        if (!validation.isValid()) {
            AppLogger.logWarning("Invalid costume data: %s", validation.getMessage());
            return null;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            String query = "INSERT INTO " + AppConstants.Database.TABLE_COSTUME + 
                          " (NamaKarakter, AsalKarakter, Ukuran, Stok, Harga) VALUES (?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, InputValidator.sanitizeInput(characterName));
            pstmt.setString(2, InputValidator.sanitizeInput(characterOrigin));
            pstmt.setString(3, size);
            pstmt.setInt(4, stock);
            pstmt.setBigDecimal(5, price);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int costumeId = rs.getInt(1);
                    
                    Costume costume = new Costume(characterName, characterOrigin, 
                                                Costume.Size.valueOf(size), stock, price);
                    costume.setCostumeId(costumeId);
                    
                    AppLogger.logDatabaseOperation("INSERT", AppConstants.Database.TABLE_COSTUME, true);
                    AppLogger.logUserAction("SYSTEM", AppConstants.Actions.CREATE, "Costume", 
                                          String.valueOf(costumeId), 
                                          String.format("New costume created: %s", characterName));
                    
                    return costume;
                }
            }
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("createCostume", AppConstants.Database.TABLE_COSTUME, e);
            AppLogger.logError("Error creating costume: %s", e, characterName);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Update existing costume
     */
    public boolean updateCostume(int costumeId, String characterName, String characterOrigin, 
                                String size, int stock, BigDecimal price) {
        AppLogger.logDebug("Updating costume ID: %d", costumeId);
        
        // Input validation
        InputValidator.ValidationResult validation = validateCostumeData(characterName, characterOrigin, size, stock, price);
        if (!validation.isValid()) {
            AppLogger.logWarning("Invalid costume data for update: %s", validation.getMessage());
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            String query = "UPDATE " + AppConstants.Database.TABLE_COSTUME + 
                          " SET NamaKarakter = ?, AsalKarakter = ?, Ukuran = ?, Stok = ?, Harga = ? " +
                          "WHERE IDKostum = ?";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, InputValidator.sanitizeInput(characterName));
            pstmt.setString(2, InputValidator.sanitizeInput(characterOrigin));
            pstmt.setString(3, size);
            pstmt.setInt(4, stock);
            pstmt.setBigDecimal(5, price);
            pstmt.setInt(6, costumeId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                AppLogger.logDatabaseOperation("UPDATE", AppConstants.Database.TABLE_COSTUME, true);
                AppLogger.logUserAction("SYSTEM", AppConstants.Actions.UPDATE, "Costume", 
                                      String.valueOf(costumeId), 
                                      String.format("Costume updated: %s", characterName));
                return true;
            } else {
                AppLogger.logWarning("No costume found with ID: %d for update", costumeId);
            }
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("updateCostume", AppConstants.Database.TABLE_COSTUME, e);
            AppLogger.logError("Error updating costume ID: %d", e, costumeId);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Delete costume
     */
    public boolean deleteCostume(int costumeId) {
        AppLogger.logDebug("Deleting costume ID: %d", costumeId);
        
        // Check if costume is currently rented
        if (isCostumeCurrentlyRented(costumeId)) {
            AppLogger.logWarning("Cannot delete costume ID %d: currently rented", costumeId);
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            String query = "DELETE FROM " + AppConstants.Database.TABLE_COSTUME + " WHERE IDKostum = ?";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, costumeId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                AppLogger.logDatabaseOperation("DELETE", AppConstants.Database.TABLE_COSTUME, true);
                AppLogger.logUserAction("SYSTEM", AppConstants.Actions.DELETE, "Costume", 
                                      String.valueOf(costumeId), "Costume deleted");
                return true;
            } else {
                AppLogger.logWarning("No costume found with ID: %d for deletion", costumeId);
            }
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("deleteCostume", AppConstants.Database.TABLE_COSTUME, e);
            AppLogger.logError("Error deleting costume ID: %d", e, costumeId);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Search costumes by character name
     */
    public List<Costume> searchCostumes(String searchTerm) {
        AppLogger.logDebug("Searching costumes with term: %s", searchTerm);
        
        List<Costume> costumes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            String query = "SELECT IDKostum, NamaKarakter, AsalKarakter, Ukuran, Stok, Harga " +
                          "FROM " + AppConstants.Database.TABLE_COSTUME + 
                          " WHERE NamaKarakter LIKE ? OR AsalKarakter LIKE ? " +
                          "ORDER BY NamaKarakter";
            
            pstmt = conn.prepareStatement(query);
            String searchPattern = "%" + InputValidator.sanitizeInput(searchTerm) + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Costume costume = mapResultSetToCostume(rs);
                costumes.add(costume);
            }
            
            AppLogger.logDatabaseOperation("SELECT", AppConstants.Database.TABLE_COSTUME, true);
            AppLogger.logDebug("Found %d costumes matching search term: %s", costumes.size(), searchTerm);
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("searchCostumes", AppConstants.Database.TABLE_COSTUME, e);
            AppLogger.logError("Error searching costumes with term: %s", e, searchTerm);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, rs);
        }
        
        return costumes;
    }
    
    /**
     * Check if costume is available for rental
     */
    public boolean isCostumeAvailable(int costumeId, int quantity) {
        Costume costume = getCostumeById(costumeId);
        return costume != null && costume.canRent(quantity);
    }
    
    /**
     * Reserve costume stock for rental
     */
    public boolean reserveStock(int costumeId, int quantity) {
        AppLogger.logDebug("Reserving %d units of costume ID: %d", quantity, costumeId);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            // Check current stock
            String selectQuery = "SELECT Stok FROM " + AppConstants.Database.TABLE_COSTUME + " WHERE IDKostum = ?";
            pstmt = conn.prepareStatement(selectQuery);
            pstmt.setInt(1, costumeId);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int currentStock = rs.getInt("Stok");
                
                if (currentStock >= quantity) {
                    // Update stock
                    rs.close();
                    pstmt.close();
                    
                    String updateQuery = "UPDATE " + AppConstants.Database.TABLE_COSTUME + 
                                       " SET Stok = Stok - ? WHERE IDKostum = ?";
                    pstmt = conn.prepareStatement(updateQuery);
                    pstmt.setInt(1, quantity);
                    pstmt.setInt(2, costumeId);
                    
                    int affectedRows = pstmt.executeUpdate();
                    
                    if (affectedRows > 0) {
                        AppLogger.logUserAction("SYSTEM", "RESERVE_STOCK", "Costume", 
                                              String.valueOf(costumeId), 
                                              String.format("Reserved %d units", quantity));
                        return true;
                    }
                } else {
                    AppLogger.logWarning("Insufficient stock for costume ID %d: requested %d, available %d", 
                                       costumeId, quantity, currentStock);
                }
            }
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("reserveStock", AppConstants.Database.TABLE_COSTUME, e);
            AppLogger.logError("Error reserving stock for costume ID: %d", e, costumeId);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Return costume stock
     */
    public boolean returnStock(int costumeId, int quantity) {
        AppLogger.logDebug("Returning %d units of costume ID: %d", quantity, costumeId);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            String query = "UPDATE " + AppConstants.Database.TABLE_COSTUME + 
                          " SET Stok = Stok + ? WHERE IDKostum = ?";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, costumeId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                AppLogger.logUserAction("SYSTEM", "RETURN_STOCK", "Costume", 
                                      String.valueOf(costumeId), 
                                      String.format("Returned %d units", quantity));
                return true;
            }
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("returnStock", AppConstants.Database.TABLE_COSTUME, e);
            AppLogger.logError("Error returning stock for costume ID: %d", e, costumeId);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Check if costume is currently rented
     */
    private boolean isCostumeCurrentlyRented(int costumeId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            
            // Check active rentals
            String query = "SELECT COUNT(*) FROM " + AppConstants.Database.TABLE_RENTAL + 
                          " WHERE IDKostum = ? AND TanggalPengembalian >= CURDATE()";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, costumeId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            AppLogger.logDatabaseError("isCostumeCurrentlyRented", AppConstants.Database.TABLE_RENTAL, e);
        } finally {
            DatabaseManager.closeResources(conn, pstmt, rs);
        }
        
        return false;
    }
    
    /**
     * Map ResultSet to Costume object
     */
    private Costume mapResultSetToCostume(ResultSet rs) throws SQLException {
        Costume costume = new Costume();
        costume.setCostumeId(rs.getInt("IDKostum"));
        costume.setCharacterName(rs.getString("NamaKarakter"));
        costume.setCharacterOrigin(rs.getString("AsalKarakter"));
        costume.setSize(Costume.Size.valueOf(rs.getString("Ukuran")));
        costume.setStock(rs.getInt("Stok"));
        costume.setAvailableStock(rs.getInt("Stok")); // Assuming available = total for now
        costume.setPrice(rs.getBigDecimal("Harga"));
        
        return costume;
    }
    
    /**
     * Validate costume data
     */
    private InputValidator.ValidationResult validateCostumeData(String characterName, String characterOrigin, 
                                                               String size, int stock, BigDecimal price) {
        // Validate character name
        if (!InputValidator.isValidLength(characterName, 
                AppConstants.Business.MIN_COSTUME_NAME_LENGTH, 
                AppConstants.Business.MAX_COSTUME_NAME_LENGTH)) {
            return new InputValidator.ValidationResult(false, "Nama karakter tidak valid");
        }
        
        // Validate character origin
        if (!InputValidator.isNotEmpty(characterOrigin)) {
            return new InputValidator.ValidationResult(false, "Asal karakter harus diisi");
        }
        
        // Validate size
        try {
            Costume.Size.valueOf(size);
        } catch (IllegalArgumentException e) {
            return new InputValidator.ValidationResult(false, "Ukuran tidak valid");
        }
        
        // Validate stock
        if (stock < 0 || stock > AppConstants.Business.MAX_COSTUME_STOCK) {
            return new InputValidator.ValidationResult(false, "Stok tidak valid");
        }
        
        // Validate price
        if (price == null || price.compareTo(AppConstants.Business.MIN_COSTUME_PRICE) < 0 || 
            price.compareTo(AppConstants.Business.MAX_COSTUME_PRICE) > 0) {
            return new InputValidator.ValidationResult(false, "Harga tidak valid");
        }
        
        return new InputValidator.ValidationResult(true, "Valid");
    }
}