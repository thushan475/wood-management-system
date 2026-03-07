package lk.ijse.wood_managment.Model;

import lk.ijse.wood_managment.Dto.CuttingDTO;
import lk.ijse.wood_managment.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Cutting {
    public static boolean save(CuttingDTO dto) throws SQLException {
        String sql = "INSERT INTO cutting (wood_id, cutting_date, qtyproduced, description) VALUES (?, ?, ?, ?)";
        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stm.setInt(1, dto.getWoodId());
        stm.setDate(2, Date.valueOf(dto.getCuttingDate()));
        stm.setInt(3, dto.getQty());
        stm.setString(4, dto.getDescription());
        int affectedRows = stm.executeUpdate();
        if (affectedRows > 0) {
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                dto.setCuttingId(rs.getInt(1));
            }
            return true;
        }
        return false;
    }

    public static boolean update(CuttingDTO dto) throws SQLException {
        String sql = "UPDATE cutting SET wood_id=?, cutting_date=?, qtyproduced=?, description=? WHERE cutting_id=?";
        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);
        stm.setInt(1, dto.getWoodId());
        stm.setDate(2, Date.valueOf(dto.getCuttingDate()));
        stm.setInt(3, dto.getQty());
        stm.setString(4, dto.getDescription());
        stm.setInt(5, dto.getCuttingId());
        return stm.executeUpdate() > 0;
    }

    public static boolean delete(int cuttingId) throws SQLException {
        String sql = "DELETE FROM cutting WHERE cutting_id=?";
        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);
        stm.setInt(1, cuttingId);
        return stm.executeUpdate() > 0;
    }

    public static CuttingDTO searchByWoodId(int woodId) throws SQLException {
        String sql = "SELECT * FROM cutting WHERE wood_id=?";
        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement stm = con.prepareStatement(sql);
        stm.setInt(1, woodId);
        ResultSet rs = stm.executeQuery();
        if (rs.next()) {
            return new CuttingDTO(
                    rs.getInt("cutting_id"),
                    rs.getInt("wood_id"),
                    rs.getInt("qtyproduced"),
                    rs.getDate("cutting_date").toLocalDate(),
                    rs.getString("description")
            );
        }
        return null;
    }

    public static List<CuttingDTO> getAll() throws SQLException {
        String sql = "SELECT * FROM cutting";
        Connection con = DBConnection.getInstance().getConnection();
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        List<CuttingDTO> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new CuttingDTO(
                    rs.getInt("cutting_id"),
                    rs.getInt("wood_id"),
                    rs.getInt("qtyproduced"),
                    rs.getDate("cutting_date").toLocalDate(),
                    rs.getString("description")
            ));
        }
        return list;
    }
}
