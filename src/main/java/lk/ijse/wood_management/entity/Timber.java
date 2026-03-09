package lk.ijse.wood_management.entity;

public class Timber {
    private int timberId;
    private int cuttingId;
    private double quantity;
    private double width;
    private double length;
    private String species;

    public Timber(int timberId, int cuttingId, double quantity, double width, double length, String species) {
        this.timberId = timberId; this.cuttingId = cuttingId;
        this.quantity = quantity; this.width = width;
        this.length = length; this.species = species;
    }

    public int getTimberId() { return timberId; }
    public int getCuttingId() { return cuttingId; }
    public double getQuantity() { return quantity; }
    public double getWidth() { return width; }
    public double getLength() { return length; }
    public String getSpecies() { return species; }
    public void setTimberId(int timberId) { this.timberId = timberId; }
    public void setCuttingId(int cuttingId) { this.cuttingId = cuttingId; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public void setWidth(double width) { this.width = width; }
    public void setLength(double length) { this.length = length; }
    public void setSpecies(String species) { this.species = species; }
}
