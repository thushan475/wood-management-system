package lk.ijse.wood_management.dao.custom.impl;

import lk.ijse.wood_management.dao.custom.CuttingDAO;

import lk.ijse.wood_management.db.DBConnection;
import lk.ijse.wood_management.dto.CuttingDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CuttingDAOImpl implements CuttingDAO {

    public boolean save(CuttingDTO dto) throws SQLException {
        String sql = "INSERT INTO cutting (wood_id, cutting_date, qtyproduced, description) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, dto.getWoodId());
            pst.setDate(2, Date.valueOf(dto.getCuttingDate()));
            pst.setInt(3, dto.getQty());
            pst.setString(4, dto.getDescription());
            int rows = pst.executeUpdate();
            if (rows > 0) {
                ResultSet keys = pst.getGeneratedKeys();
                if (keys.next()) dto.setCuttingId(keys.getInt(1));
                return true;
            }
            return false;
        }
    }

    public boolean update(CuttingDTO dto) throws SQLException {
        String sql = "UPDATE cutting SET wood_id=?, cutting_date=?, qtyproduced=?, description=? WHERE cutting_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, dto.getWoodId());
            pst.setDate(2, Date.valueOf(dto.getCuttingDate()));
            pst.setInt(3, dto.getQty());
            pst.setString(4, dto.getDescription());
            pst.setInt(5, dto.getCuttingId());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean delete(int cuttingId) throws SQLException {
        String sql = "DELETE FROM cutting WHERE cutting_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, cuttingId);
            return pst.executeUpdate() > 0;
        }
    }

    public List<CuttingDTO> getAll() throws SQLException {
        List<CuttingDTO> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
             try (ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM cutting")) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public CuttingDTO findByWoodId(int woodId) throws SQLException {
        String sql = "SELECT * FROM cutting WHERE wood_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, woodId);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    private CuttingDTO mapRow(ResultSet rs) throws SQLException {
        return new CuttingDTO(
                rs.getInt("cutting_id"), rs.getInt("wood_id"),
                rs.getInt("qtyproduced"), rs.getDate("cutting_date").toLocalDate(),
                rs.getString("description")
        );
    }
}
