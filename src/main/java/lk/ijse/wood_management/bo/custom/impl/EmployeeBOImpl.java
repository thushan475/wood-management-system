package lk.ijse.wood_management.bo.custom.impl;

import lk.ijse.wood_management.bo.custom.EmployeeBO;

import lk.ijse.wood_management.entity.Employee;
import lk.ijse.wood_management.dao.custom.impl.EmployeeDAOImpl;

import java.sql.SQLException;
import java.util.List;

public class EmployeeBOImpl implements EmployeeBO {
    private final EmployeeDAOImpl empRepo = new EmployeeDAOImpl();

    public List<Employee> getAll() throws SQLException { return empRepo.getAll(); }

    public boolean addEmployee(Employee e) throws SQLException {
        if (!e.getContact().matches("\\d{10}"))
            throw new IllegalArgumentException("Contact number must contain exactly 10 digits");
        return empRepo.save(e);
    }

    public boolean updateEmployee(Employee e) throws SQLException {
        if (!e.getContact().matches("\\d{10}"))
            throw new IllegalArgumentException("Contact number must contain exactly 10 digits");
        return empRepo.update(e);
    }

    public boolean deleteEmployee(int id) throws SQLException { return empRepo.delete(id); }
    public Employee findById(int id) throws SQLException { return empRepo.findById(id); }
    public Employee findByName(String name) throws SQLException { return empRepo.findByName(name); }
}
