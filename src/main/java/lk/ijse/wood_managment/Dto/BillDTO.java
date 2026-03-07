package lk.ijse.wood_managment.Dto;

import java.time.LocalDate;

public class BillDTO {
    private int billId;
    private int orderId;
    private double amount;
    private LocalDate billDate;
    private double totalAmount;

    public BillDTO(int billId, int orderId, double amount, LocalDate billDate, double totalAmount) {
        this.billId = billId;
        this.orderId = orderId;
        this.amount = amount;
        this.billDate = billDate;
        this.totalAmount = totalAmount;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
