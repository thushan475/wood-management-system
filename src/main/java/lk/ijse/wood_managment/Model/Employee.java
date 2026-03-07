package lk.ijse.wood_managment.Model;

import javafx.beans.property.*;

public class Employee {

    private IntegerProperty employeeId;
    private IntegerProperty userId;
    private StringProperty name;
    private StringProperty role;
    private StringProperty contact;
    private DoubleProperty salary;

    public Employee(int employeeId, int userId, String name, String role, String contact, double salary) {
        this.employeeId = new SimpleIntegerProperty(employeeId);
        this.userId = new SimpleIntegerProperty(userId);
        this.name = new SimpleStringProperty(name);
        this.role = new SimpleStringProperty(role);
        this.contact = new SimpleStringProperty(contact);
        this.salary = new SimpleDoubleProperty(salary);
    }

    public IntegerProperty employeeIdProperty() { return employeeId; }
    public IntegerProperty userIdProperty() { return userId; }
    public StringProperty nameProperty() { return name; }
    public StringProperty roleProperty() { return role; }
    public StringProperty contactProperty() { return contact; }
    public DoubleProperty salaryProperty() { return salary; }

    public int getEmployeeId() { return employeeId.get(); }
    public void setEmployeeId(int employeeId) { this.employeeId.set(employeeId); }

    public int getUserId() { return userId.get(); }
    public void setUserId(int userId) { this.userId.set(userId); }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    public String getRole() { return role.get(); }
    public void setRole(String role) { this.role.set(role); }

    public String getContact() { return contact.get(); }
    public void setContact(String contact) { this.contact.set(contact); }

    public double getSalary() { return salary.get(); }
    public void setSalary(double salary) { this.salary.set(salary); }
}
