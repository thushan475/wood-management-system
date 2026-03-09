package lk.ijse.wood_management.dao.custom.impl;

import lk.ijse.wood_management.dao.custom.CustomerDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.wood_management.db.DBConnection;
import lk.ijse.wood_management.dto.CustomerDTO;

import java.sql.*;

public class CustomerDAOImpl implements CustomerDAO {

    public ObservableList<CustomerDTO> getAll() throws SQLException {
        ObservableList<CustomerDTO> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customer";
        Connection conn = DBConnection.getInstance().getConnection();
             try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean save(CustomerDTO dto) throws SQLException {
        String sql = "INSERT INTO customer(user_id, name, contact_number, address) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, dto.getUserId());
            pst.setString(2, dto.getName());
            pst.setString(3, dto.getContact());
            pst.setString(4, dto.getLocation());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean update(CustomerDTO dto) throws SQLException {
        String sql = "UPDATE customer SET user_id=?, name=?, contact_number=?, address=? WHERE customer_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, dto.getUserId());
            pst.setString(2, dto.getName());
            pst.setString(3, dto.getContact());
            pst.setString(4, dto.getLocation());
            pst.setInt(5, dto.getCustomerId());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean delete(int customerId) throws SQLException {
        String sql = "DELETE FROM customer WHERE customer_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, customerId);
            return pst.executeUpdate() > 0;
        }
    }

    public CustomerDTO findById(int customerId) throws SQLException {
        String sql = "SELECT * FROM customer WHERE customer_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    public CustomerDTO findByName(String name) throws SQLException {
        String sql = "SELECT * FROM customer WHERE name=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    public boolean hasOrders(int customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customer_order WHERE customer_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private CustomerDTO mapRow(ResultSet rs) throws SQLException {
        return new CustomerDTO(
                rs.getInt("user_id"),
                rs.getInt("customer_id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("contact_number")
        );
    }
}
