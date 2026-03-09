package lk.ijse.wood_management.entity;

import javafx.beans.property.*;

public class Employee {
    private IntegerProperty employeeId = new SimpleIntegerProperty();
    private IntegerProperty userId = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty role = new SimpleStringProperty();
    private StringProperty contact = new SimpleStringProperty();
    private DoubleProperty salary = new SimpleDoubleProperty();

    public Employee(int employeeId, int userId, String name, String role, String contact, double salary) {
        this.employeeId.set(employeeId); this.userId.set(userId);
        this.name.set(name); this.role.set(role);
        this.contact.set(contact); this.salary.set(salary);
    }

    public IntegerProperty employeeIdProperty() { return employeeId; }
    public IntegerProperty userIdProperty() { return userId; }
    public StringProperty nameProperty() { return name; }
    public StringProperty roleProperty() { return role; }
    public StringProperty contactProperty() { return contact; }
    public DoubleProperty salaryProperty() { return salary; }

    public int getEmployeeId() { return employeeId.get(); }
    public int getUserId() { return userId.get(); }
    public String getName() { return name.get(); }
    public String getRole() { return role.get(); }
    public String getContact() { return contact.get(); }
    public double getSalary() { return salary.get(); }

    public void setEmployeeId(int v) { employeeId.set(v); }
    public void setUserId(int v) { userId.set(v); }
    public void setName(String v) { name.set(v); }
    public void setRole(String v) { role.set(v); }
    public void setContact(String v) { contact.set(v); }
    public void setSalary(double v) { salary.set(v); }
}
