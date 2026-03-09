package lk.ijse.wood_management.dto;

import java.time.LocalDate;

public class CuttingDTO {
    private int cuttingId;
    private int woodId;
    private int qty;
    private LocalDate cuttingDate;
    private String description;

    public CuttingDTO(int cuttingId, int woodId, int qty, LocalDate cuttingDate, String description) {
        this.cuttingId = cuttingId;
        this.woodId = woodId;
        this.qty = qty;
        this.cuttingDate = cuttingDate;
        this.description = description;
    }

    public int getCuttingId() { return cuttingId; }
    public void setCuttingId(int cuttingId) { this.cuttingId = cuttingId; }
    public int getWoodId() { return woodId; }
    public void setWoodId(int woodId) { this.woodId = woodId; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public LocalDate getCuttingDate() { return cuttingDate; }
    public void setCuttingDate(LocalDate cuttingDate) { this.cuttingDate = cuttingDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
