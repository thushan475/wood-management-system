package lk.ijse.wood_management.dao.custom.impl;

import lk.ijse.wood_management.dao.custom.ExpenseTypeDAO;

import lk.ijse.wood_management.db.DBConnection;
import lk.ijse.wood_management.dto.ExpenseTypeDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseTypeDAOImpl implements ExpenseTypeDAO {

    public boolean save(ExpenseTypeDTO dto) throws SQLException {
        String sql = "INSERT INTO expense_type (expense_type_id, expense_name) VALUES (?, ?)";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, dto.getExpenseId());
            pst.setString(2, dto.getExpenseName());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean update(ExpenseTypeDTO dto) throws SQLException {
        String sql = "UPDATE expense_type SET expense_name=? WHERE expense_type_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, dto.getExpenseName());
            pst.setInt(2, dto.getExpenseId());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM expense_type WHERE expense_type_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        }
    }

    public ExpenseTypeDTO findById(int id) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM expense_type WHERE expense_type_id=?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    public List<ExpenseTypeDTO> getAll() throws SQLException {
        List<ExpenseTypeDTO> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
             try (ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM expense_type")) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private ExpenseTypeDTO mapRow(ResultSet rs) throws SQLException {
        return new ExpenseTypeDTO(rs.getInt("expense_type_id"), rs.getString("expense_name"));
    }
}
