package lk.ijse.wood_management.dao.custom.impl;

import lk.ijse.wood_management.dao.custom.WoodDAO;

import lk.ijse.wood_management.db.DBConnection;
import lk.ijse.wood_management.dto.WoodDTO;
import lk.ijse.wood_management.entity.Wood;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WoodDAOImpl implements WoodDAO {

    public List<Wood> getAll() throws SQLException {
        List<Wood> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
             try (ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM wood")) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public int save(WoodDTO dto) throws SQLException {
        String sql = "INSERT INTO wood (supplier_id, species, length, width, purchaseDate, unitprice) VALUES (?,?,?,?,?,?)";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, dto.getSupplierId());
            pst.setString(2, dto.getSpecies());
            pst.setDouble(3, dto.getLength());
            pst.setDouble(4, dto.getWidth());
            pst.setDate(5, dto.getPurchaseDate());
            pst.setDouble(6, dto.getUnitPrice());
            pst.executeUpdate();
            ResultSet keys = pst.getGeneratedKeys();
            return keys.next() ? keys.getInt(1) : -1;
        }
    }

    public boolean update(WoodDTO dto) throws SQLException {
        String sql = "UPDATE wood SET supplier_id=?, species=?, length=?, width=?, purchaseDate=?, unitprice=? WHERE wood_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, dto.getSupplierId());
            pst.setString(2, dto.getSpecies());
            pst.setDouble(3, dto.getLength());
            pst.setDouble(4, dto.getWidth());
            pst.setDate(5, dto.getPurchaseDate());
            pst.setDouble(6, dto.getUnitPrice());
            pst.setInt(7, dto.getWoodId());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean delete(int woodId) throws SQLException {
        String sql = "DELETE FROM wood WHERE wood_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, woodId);
            return pst.executeUpdate() > 0;
        }
    }

    public List<Wood> search(String species, Integer supplierId) throws SQLException {
        List<Wood> list = new ArrayList<>();
        String sql = "SELECT * FROM wood WHERE 1=1";
        if (species != null && !species.isEmpty()) sql += " AND species LIKE ?";
        if (supplierId != null) sql += " AND supplier_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            int idx = 1;
            if (species != null && !species.isEmpty()) pst.setString(idx++, "%" + species + "%");
            if (supplierId != null) pst.setInt(idx, supplierId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Wood mapRow(ResultSet rs) throws SQLException {
        return new Wood(
                rs.getInt("wood_id"), rs.getInt("supplier_id"),
                rs.getString("species"), rs.getDate("purchaseDate"),
                rs.getDouble("length"), rs.getDouble("width"), rs.getDouble("unitprice")
        );
    }
}
