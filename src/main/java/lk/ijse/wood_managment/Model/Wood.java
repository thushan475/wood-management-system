package lk.ijse.wood_managment.Model;

import java.sql.Date;

public class Wood {

    private int woodId;
    private int supplierId;
    private String species;
    private Date purchaseDate;
    private double length;
    private double width;
    private double unitPrice;

    public Wood(int woodId, int supplierId, String species, Date purchaseDate,
                double length, double width, double unitPrice) {
        this.woodId = woodId;
        this.supplierId = supplierId;
        this.species = species;
        this.purchaseDate = purchaseDate;
        this.length = length;
        this.width = width;
        this.unitPrice = unitPrice;
    }

    public int getWoodId() { return woodId; }
    public int getSupplierId() { return supplierId; }
    public String getSpecies() { return species; }
    public Date getPurchaseDate() { return purchaseDate; }
    public double getLength() { return length; }
    public double getWidth() { return width; }
    public double getUnitPrice() { return unitPrice; }
}