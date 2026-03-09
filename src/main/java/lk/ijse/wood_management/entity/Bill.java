package lk.ijse.wood_management.entity;

import java.time.LocalDate;

public class Bill {
    private int billId;
    private int orderId;
    private double amount;
    private LocalDate billDate;
    private String description;

    public Bill(int billId, int orderId, double amount, LocalDate billDate, String description) {
        this.billId = billId; this.orderId = orderId;
        this.amount = amount; this.billDate = billDate; this.description = description;
    }

    public int getBillId() { return billId; }
    public int getOrderId() { return orderId; }
    public double getAmount() { return amount; }
    public LocalDate getBillDate() { return billDate; }
    public String getDescription() { return description; }
    public void setBillId(int billId) { this.billId = billId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }
    public void setDescription(String description) { this.description = description; }
}
