package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Costume model class representing costume entity
 */
public class Costume {
    private int costumeId;
    private String characterName;
    private String characterOrigin;
    private Size size;
    private int stock;
    private int availableStock;
    private BigDecimal price;
    private CostumeStatus status;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public Costume() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = CostumeStatus.AVAILABLE;
        this.availableStock = 0;
    }
    
    public Costume(String characterName, String characterOrigin, Size size, int stock, BigDecimal price) {
        this();
        this.characterName = characterName;
        this.characterOrigin = characterOrigin;
        this.size = size;
        this.stock = stock;
        this.availableStock = stock;
        this.price = price;
    }
    
    // Getters and Setters
    public int getCostumeId() {
        return costumeId;
    }
    
    public void setCostumeId(int costumeId) {
        this.costumeId = costumeId;
    }
    
    public String getCharacterName() {
        return characterName;
    }
    
    public void setCharacterName(String characterName) {
        this.characterName = characterName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCharacterOrigin() {
        return characterOrigin;
    }
    
    public void setCharacterOrigin(String characterOrigin) {
        this.characterOrigin = characterOrigin;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Size getSize() {
        return size;
    }
    
    public void setSize(Size size) {
        this.size = size;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getAvailableStock() {
        return availableStock;
    }
    
    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
        this.updatedAt = LocalDateTime.now();
    }
    
    public CostumeStatus getStatus() {
        return status;
    }
    
    public void setStatus(CostumeStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business Methods
    public boolean isAvailable() {
        return status == CostumeStatus.AVAILABLE && availableStock > 0;
    }
    
    public boolean canRent(int quantity) {
        return isAvailable() && availableStock >= quantity;
    }
    
    public void reserveStock(int quantity) throws IllegalArgumentException {
        if (!canRent(quantity)) {
            throw new IllegalArgumentException("Insufficient stock or costume not available");
        }
        this.availableStock -= quantity;
        if (this.availableStock == 0) {
            this.status = CostumeStatus.OUT_OF_STOCK;
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    public void returnStock(int quantity) {
        this.availableStock += quantity;
        if (this.availableStock > this.stock) {
            this.availableStock = this.stock;
        }
        if (this.availableStock > 0 && this.status == CostumeStatus.OUT_OF_STOCK) {
            this.status = CostumeStatus.AVAILABLE;
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal calculateRentalCost(int quantity, int days) {
        return price.multiply(BigDecimal.valueOf(quantity)).multiply(BigDecimal.valueOf(days));
    }
    
    @Override
    public String toString() {
        return "Costume{" +
                "costumeId=" + costumeId +
                ", characterName='" + characterName + '\'' +
                ", characterOrigin='" + characterOrigin + '\'' +
                ", size=" + size +
                ", stock=" + stock +
                ", availableStock=" + availableStock +
                ", price=" + price +
                ", status=" + status +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Costume costume = (Costume) o;
        return costumeId == costume.costumeId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(costumeId);
    }
    
    // Enums
    public enum Size {
        S("Small"),
        M("Medium"),
        L("Large"),
        XL("Extra Large"),
        ALL_SIZE("All Size");
        
        private final String displayName;
        
        Size(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public enum CostumeStatus {
        AVAILABLE("Available"),
        OUT_OF_STOCK("Out of Stock"),
        MAINTENANCE("Under Maintenance"),
        DISCONTINUED("Discontinued");
        
        private final String displayName;
        
        CostumeStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
}