package lk.ijse.wood_management.dao.custom.impl;

import lk.ijse.wood_management.dao.custom.SupplierDAO;

import lk.ijse.wood_management.db.DBConnection;
import lk.ijse.wood_management.dto.SupplierDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAOImpl implements SupplierDAO {

    public List<SupplierDTO> getAll() throws SQLException {
        List<SupplierDTO> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
             try (ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM supplier")) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public boolean save(SupplierDTO dto) throws SQLException {
        int newId = getNextId();
        String sql = "INSERT INTO supplier VALUES (?,?,?,?)";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, newId);
            pst.setString(2, dto.getName());
            pst.setString(3, dto.getContactNumber());
            pst.setString(4, dto.getDescription());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean update(SupplierDTO dto) throws SQLException {
        String sql = "UPDATE supplier SET name=?, contact_number=?, description=? WHERE supplier_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, dto.getName());
            pst.setString(2, dto.getContactNumber());
            pst.setString(3, dto.getDescription());
            pst.setInt(4, dto.getSupplierId());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean delete(int supplierId) throws SQLException {
        String sql = "DELETE FROM supplier WHERE supplier_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, supplierId);
            return pst.executeUpdate() > 0;
        }
    }

    public List<SupplierDTO> searchByName(String name) throws SQLException {
        List<SupplierDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM supplier WHERE name LIKE ?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "%" + name + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Integer> getAllSupplierIds() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
             try (ResultSet rs = conn.createStatement().executeQuery("SELECT supplier_id FROM supplier")) {
            while (rs.next()) ids.add(rs.getInt("supplier_id"));
        }
        return ids;
    }

    private int getNextId() throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
             try (ResultSet rs = conn.createStatement().executeQuery("SELECT MAX(supplier_id) FROM supplier")) {
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }

    private SupplierDTO mapRow(ResultSet rs) throws SQLException {
        return new SupplierDTO(
                rs.getInt("supplier_id"),
                rs.getString("name"),
                rs.getString("contact_number"),
                rs.getString("description")
        );
    }
}
