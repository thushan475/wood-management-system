package lk.ijse.wood_managment.Model;

import java.time.LocalDate;

public class CustomerOrder {
    private int orderId;
    private int customerId;
    private LocalDate orderDate;
    private double totalAmount;
    private String description;

    public CustomerOrder(int orderId, int customerId, LocalDate orderDate, double totalAmount, String description) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.description = description;
    }


    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
