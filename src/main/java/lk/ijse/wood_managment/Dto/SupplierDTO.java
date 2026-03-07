package lk.ijse.wood_managment.Dto;

public class SupplierDTO {
    private int supplierId;
    private String name;
    private String contactNumber;
    private String description;

    public SupplierDTO(int supplierId, String name, String contactNumber, String description) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.description = description;
    }

    public int getSupplierId() { return supplierId; }
    public String getName() { return name; }
    public String getContactNumber() { return contactNumber; }
    public String getDescription() { return description; }
}
