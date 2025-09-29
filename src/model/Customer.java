package model;

import java.time.LocalDateTime;

/**
 * Customer model class representing customer entity
 */
public class Customer {
    private int customerId;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String instagramAccount;
    private CustomerStatus status;
    private int totalRentals;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastRentalDate;
    
    // Constructors
    public Customer() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = CustomerStatus.ACTIVE;
        this.totalRentals = 0;
    }
    
    public Customer(String name, String address, String phoneNumber, String instagramAccount) {
        this();
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.instagramAccount = instagramAccount;
    }
    
    public Customer(String name, String address, String phoneNumber, String email, String instagramAccount) {
        this(name, address, phoneNumber, instagramAccount);
        this.email = email;
    }
    
    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getInstagramAccount() {
        return instagramAccount;
    }
    
    public void setInstagramAccount(String instagramAccount) {
        this.instagramAccount = instagramAccount;
        this.updatedAt = LocalDateTime.now();
    }
    
    public CustomerStatus getStatus() {
        return status;
    }
    
    public void setStatus(CustomerStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getTotalRentals() {
        return totalRentals;
    }
    
    public void setTotalRentals(int totalRentals) {
        this.totalRentals = totalRentals;
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
    
    public LocalDateTime getLastRentalDate() {
        return lastRentalDate;
    }
    
    public void setLastRentalDate(LocalDateTime lastRentalDate) {
        this.lastRentalDate = lastRentalDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Business Methods
    public boolean isActive() {
        return status == CustomerStatus.ACTIVE;
    }
    
    public void incrementRentalCount() {
        this.totalRentals++;
        this.lastRentalDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public CustomerTier getCustomerTier() {
        if (totalRentals >= 50) {
            return CustomerTier.PLATINUM;
        } else if (totalRentals >= 20) {
            return CustomerTier.GOLD;
        } else if (totalRentals >= 5) {
            return CustomerTier.SILVER;
        } else {
            return CustomerTier.BRONZE;
        }
    }
    
    public double getDiscountRate() {
        return getCustomerTier().getDiscountRate();
    }
    
    public boolean canRent() {
        return isActive();
    }
    
    public String getDisplayName() {
        return name + (instagramAccount != null ? " (@" + instagramAccount + ")" : "");
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", instagramAccount='" + instagramAccount + '\'' +
                ", status=" + status +
                ", totalRentals=" + totalRentals +
                ", tier=" + getCustomerTier() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerId == customer.customerId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(customerId);
    }
    
    // Enums
    public enum CustomerStatus {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        BLACKLISTED("Blacklisted"),
        SUSPENDED("Suspended");
        
        private final String displayName;
        
        CustomerStatus(String displayName) {
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
    
    public enum CustomerTier {
        BRONZE("Bronze", 0.0),
        SILVER("Silver", 0.05),
        GOLD("Gold", 0.10),
        PLATINUM("Platinum", 0.15);
        
        private final String displayName;
        private final double discountRate;
        
        CustomerTier(String displayName, double discountRate) {
            this.displayName = displayName;
            this.discountRate = discountRate;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public double getDiscountRate() {
            return discountRate;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
}