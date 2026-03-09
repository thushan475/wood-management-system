package lk.ijse.wood_management.dto;

import java.sql.Date;

public class WoodDTO {
    private int woodId;
    private int supplierId;
    private String species;
    private double length;
    private double width;
    private Date purchaseDate;
    private double unitPrice;

    public WoodDTO(int woodId, int supplierId, String species, double length, double width, Date purchaseDate, double unitPrice) {
        this.woodId = woodId;
        this.supplierId = supplierId;
        this.species = species;
        this.length = length;
        this.width = width;
        this.purchaseDate = purchaseDate;
        this.unitPrice = unitPrice;
    }

    public int getWoodId() { return woodId; }
    public int getSupplierId() { return supplierId; }
    public String getSpecies() { return species; }
    public double getLength() { return length; }
    public double getWidth() { return width; }
    public Date getPurchaseDate() { return purchaseDate; }
    public double getUnitPrice() { return unitPrice; }

    public void setWoodId(int woodId) { this.woodId = woodId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public void setSpecies(String species) { this.species = species; }
    public void setLength(double length) { this.length = length; }
    public void setWidth(double width) { this.width = width; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
}