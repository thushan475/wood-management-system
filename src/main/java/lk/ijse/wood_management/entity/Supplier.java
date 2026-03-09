package lk.ijse.wood_management.entity;

public class Supplier {
    private int supplierId;
    private String name;
    private String contactNumber;
    private String description;

    public Supplier(int supplierId, String name, String contactNumber, String description) {
        this.supplierId = supplierId; this.name = name;
        this.contactNumber = contactNumber; this.description = description;
    }

    public int getSupplierId() { return supplierId; }
    public String getName() { return name; }
    public String getContactNumber() { return contactNumber; }
    public String getDescription() { return description; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public void setName(String name) { this.name = name; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setDescription(String description) { this.description = description; }
}
