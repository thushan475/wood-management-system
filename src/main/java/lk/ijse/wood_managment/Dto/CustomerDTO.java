package lk.ijse.wood_managment.Dto;

public class CustomerDTO {
    private int userId;
    private int customerId;
    private String name;
    private String location;
    private String contact;

    public CustomerDTO(int userId, int customerId, String name, String location, String contact) {
        this.userId = userId;
        this.customerId = customerId;
        this.name = name;
        this.location = location;
        this.contact = contact;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}
