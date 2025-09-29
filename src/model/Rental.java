package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Rental model class representing rental transaction entity
 */
public class Rental {
    private int rentalId;
    private int customerId;
    private int costumeId;
    private String customerName;
    private String costumeName;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private int quantity;
    private BigDecimal rentalCost;
    private BigDecimal shippingCost;
    private BigDecimal totalCost;
    private BigDecimal lateFee;
    private String shippingMethod;
    private String trackingNumber;
    private RentalStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public Rental() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = RentalStatus.PENDING;
        this.lateFee = BigDecimal.ZERO;
        this.shippingCost = BigDecimal.ZERO;
    }
    
    public Rental(int customerId, int costumeId, String customerName, String costumeName,
                  LocalDate rentalDate, LocalDate returnDate, int quantity, BigDecimal rentalCost) {
        this();
        this.customerId = customerId;
        this.costumeId = costumeId;
        this.customerName = customerName;
        this.costumeName = costumeName;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.quantity = quantity;
        this.rentalCost = rentalCost;
        this.totalCost = rentalCost;
    }
    
    // Getters and Setters
    public int getRentalId() {
        return rentalId;
    }
    
    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getCostumeId() {
        return costumeId;
    }
    
    public void setCostumeId(int costumeId) {
        this.costumeId = costumeId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCostumeName() {
        return costumeName;
    }
    
    public void setCostumeName(String costumeName) {
        this.costumeName = costumeName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDate getRentalDate() {
        return rentalDate;
    }
    
    public void setRentalDate(LocalDate rentalDate) {
        this.rentalDate = rentalDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }
    
    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal getRentalCost() {
        return rentalCost;
    }
    
    public void setRentalCost(BigDecimal rentalCost) {
        this.rentalCost = rentalCost;
        calculateTotalCost();
    }
    
    public BigDecimal getShippingCost() {
        return shippingCost;
    }
    
    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
        calculateTotalCost();
    }
    
    public BigDecimal getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
        this.updatedAt = LocalDateTime.now();
    }
    
    public BigDecimal getLateFee() {
        return lateFee;
    }
    
    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
        calculateTotalCost();
    }
    
    public String getShippingMethod() {
        return shippingMethod;
    }
    
    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getTrackingNumber() {
        return trackingNumber;
    }
    
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
        this.updatedAt = LocalDateTime.now();
    }
    
    public RentalStatus getStatus() {
        return status;
    }
    
    public void setStatus(RentalStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    public long getRentalDuration() {
        if (rentalDate != null && returnDate != null) {
            return ChronoUnit.DAYS.between(rentalDate, returnDate);
        }
        return 0;
    }
    
    public long getDaysLate() {
        if (actualReturnDate != null && returnDate != null) {
            long daysLate = ChronoUnit.DAYS.between(returnDate, actualReturnDate);
            return Math.max(0, daysLate);
        }
        return 0;
    }
    
    public boolean isLate() {
        return getDaysLate() > 0;
    }
    
    public boolean isOverdue() {
        return LocalDate.now().isAfter(returnDate) && status == RentalStatus.ACTIVE;
    }
    
    public void calculateLateFee(BigDecimal dailyLateFee) {
        long daysLate = getDaysLate();
        if (daysLate > 0) {
            this.lateFee = dailyLateFee.multiply(BigDecimal.valueOf(daysLate)).multiply(BigDecimal.valueOf(quantity));
        } else {
            this.lateFee = BigDecimal.ZERO;
        }
        calculateTotalCost();
    }
    
    public void calculateTotalCost() {
        this.totalCost = rentalCost.add(shippingCost).add(lateFee);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void processReturn() {
        this.actualReturnDate = LocalDate.now();
        this.status = RentalStatus.RETURNED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void confirmRental() {
        this.status = RentalStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancelRental() {
        this.status = RentalStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Rental{" +
                "rentalId=" + rentalId +
                ", customerName='" + customerName + '\'' +
                ", costumeName='" + costumeName + '\'' +
                ", rentalDate=" + rentalDate +
                ", returnDate=" + returnDate +
                ", quantity=" + quantity +
                ", status=" + status +
                ", totalCost=" + totalCost +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rental rental = (Rental) o;
        return rentalId == rental.rentalId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(rentalId);
    }
    
    // Enums
    public enum RentalStatus {
        PENDING("Pending"),
        CONFIRMED("Confirmed"),
        ACTIVE("Active"),
        RETURNED("Returned"),
        OVERDUE("Overdue"),
        CANCELLED("Cancelled"),
        COMPLETED("Completed");
        
        private final String displayName;
        
        RentalStatus(String displayName) {
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