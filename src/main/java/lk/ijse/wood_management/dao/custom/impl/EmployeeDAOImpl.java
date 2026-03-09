package lk.ijse.wood_management.dao.custom.impl;

import lk.ijse.wood_management.dao.custom.EmployeeDAO;

import lk.ijse.wood_management.db.DBConnection;
import lk.ijse.wood_management.entity.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO {

    public List<Employee> getAll() throws SQLException {
        List<Employee> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
             try (ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM employee_details")) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public boolean save(Employee e) throws SQLException {
        String sql = "INSERT INTO employee_details(employee_id, user_id, name, role, contact_number, salary) VALUES (?,?,?,?,?,?)";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, e.getEmployeeId()); pst.setInt(2, e.getUserId());
            pst.setString(3, e.getName()); pst.setString(4, e.getRole());
            pst.setString(5, e.getContact()); pst.setDouble(6, e.getSalary());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean update(Employee e) throws SQLException {
        String sql = "UPDATE employee_details SET user_id=?, name=?, role=?, contact_number=?, salary=? WHERE employee_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, e.getUserId()); pst.setString(2, e.getName());
            pst.setString(3, e.getRole()); pst.setString(4, e.getContact());
            pst.setDouble(5, e.getSalary()); pst.setInt(6, e.getEmployeeId());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM employee_details WHERE employee_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        }
    }

    public Employee findById(int id) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM employee_details WHERE employee_id=?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    public Employee findByName(String name) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM employee_details WHERE name LIKE ?")) {
            pst.setString(1, "%" + name + "%");
            ResultSet rs = pst.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("employee_id"), rs.getInt("user_id"),
                rs.getString("name"), rs.getString("role"),
                rs.getString("contact_number"), rs.getDouble("salary")
        );
    }
}
