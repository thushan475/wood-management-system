package lk.ijse.wood_managment.Model;

public class Timber {
    private int timberId;
    private int cuttingId;
    private double quantity;
    private double width;
    private double length;
    private String species;

    public Timber(int timberId, int cuttingId, double quantity, double width, double length, String species) {
        this.timberId = timberId;
        this.cuttingId = cuttingId;
        this.quantity = quantity;
        this.width = width;
        this.length = length;
        this.species = species;
    }

    public Timber(int timberId,double width, double length, String species) {
        this.timberId=timberId;
        this.width = width;
        this.length = length;
        this.species = species;
    }

    public int getTimberId() { return timberId; }
    public int getCuttingId() { return cuttingId; }
    public double getQuantity() { return quantity; }
    public double getWidth() { return width; }
    public double getLength() { return length; }
    public String getSpecies() { return species; }
}
