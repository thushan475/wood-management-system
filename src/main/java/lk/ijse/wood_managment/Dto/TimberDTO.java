package lk.ijse.wood_managment.Dto;

public class TimberDTO {

    private int timberId;
    private int cuttingId;
    private double quantity;
    private double width;
    private double length;
    private String species;

    public TimberDTO(int timberId, int cuttingId, double quantity, double width, double length, String species) {
        this.timberId = timberId;
        this.cuttingId = cuttingId;
        this.quantity = quantity;
        this.width = width;
        this.length = length;
        this.species = species;
    }

    public int getTimberId() {
        return timberId;
    }

    public int getCuttingId() {
        return cuttingId;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getWidth() {
        return width;
    }

    public double getLength() {
        return length;
    }

    public String getSpecies() {
        return species;
    }
}
