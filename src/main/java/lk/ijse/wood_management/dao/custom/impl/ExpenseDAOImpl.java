package lk.ijse.wood_management.dao.custom.impl;

import lk.ijse.wood_management.dao.custom.ExpenseDAO;

import lk.ijse.wood_management.db.DBConnection;
import lk.ijse.wood_management.dto.ExpenseDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAOImpl implements ExpenseDAO {

    public boolean save(ExpenseDTO e) throws SQLException {
        String sql = "INSERT INTO expense (wood_id, expense_type, description, species, length, width, qty_price, amount, expense_date) VALUES (?,?,?,?,?,?,?,?,?)";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            setNullableInt(pst, 1, e.getWoodId());
            pst.setString(2, e.getExpenseType());
            pst.setString(3, e.getDescription());
            setNullableString(pst, 4, e.getSpecies());
            setNullableDouble(pst, 5, e.getLength());
            setNullableDouble(pst, 6, e.getWidth());
            setNullableDouble(pst, 7, e.getQtyPrice());
            pst.setDouble(8, e.getAmount());
            pst.setDate(9, Date.valueOf(e.getExpenseDate()));
            return pst.executeUpdate() > 0;
        }
    }

    public boolean saveWithConnection(Connection conn, int woodId, String expenseType, String description,
                                      String species, double length, double width, double qtyPrice,
                                      double amount, java.time.LocalDate date) throws SQLException {
        String sql = "INSERT INTO expense (wood_id, expense_type, description, species, length, width, qty_price, amount, expense_date) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, woodId);
            pst.setString(2, expenseType);
            pst.setString(3, description);
            pst.setString(4, species);
            pst.setDouble(5, length);
            pst.setDouble(6, width);
            pst.setDouble(7, qtyPrice);
            pst.setDouble(8, amount);
            pst.setDate(9, Date.valueOf(date));
            return pst.executeUpdate() > 0;
        }
    }

    public boolean update(ExpenseDTO e) throws SQLException {
        String sql = "UPDATE expense SET expense_type=?, description=?, species=?, length=?, width=?, qty_price=?, amount=?, expense_date=? WHERE expense_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, e.getExpenseType());
            pst.setString(2, e.getDescription());
            setNullableString(pst, 3, e.getSpecies());
            setNullableDouble(pst, 4, e.getLength());
            setNullableDouble(pst, 5, e.getWidth());
            setNullableDouble(pst, 6, e.getQtyPrice());
            pst.setDouble(7, e.getAmount());
            pst.setDate(8, Date.valueOf(e.getExpenseDate()));
            pst.setInt(9, e.getExpenseId());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM expense WHERE expense_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        }
    }

    public ExpenseDTO findById(int id) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement("SELECT * FROM expense WHERE expense_id=?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    public List<ExpenseDTO> getAll() throws SQLException {
        List<ExpenseDTO> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
             try (ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM expense ORDER BY expense_date DESC, expense_id DESC")) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private void setNullableInt(PreparedStatement pst, int idx, Integer val) throws SQLException {
        if (val != null) pst.setInt(idx, val); else pst.setNull(idx, Types.INTEGER);
    }
    private void setNullableString(PreparedStatement pst, int idx, String val) throws SQLException {
        if (val != null) pst.setString(idx, val); else pst.setNull(idx, Types.VARCHAR);
    }
    private void setNullableDouble(PreparedStatement pst, int idx, Double val) throws SQLException {
        if (val != null) pst.setDouble(idx, val); else pst.setNull(idx, Types.DOUBLE);
    }

    private ExpenseDTO mapRow(ResultSet rs) throws SQLException {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setExpenseId(rs.getInt("expense_id"));
        int woodId = rs.getInt("wood_id"); dto.setWoodId(rs.wasNull() ? null : woodId);
        dto.setExpenseType(rs.getString("expense_type"));
        dto.setDescription(rs.getString("description"));
        dto.setSpecies(rs.getString("species"));
        double length = rs.getDouble("length"); dto.setLength(rs.wasNull() ? null : length);
        double width = rs.getDouble("width"); dto.setWidth(rs.wasNull() ? null : width);
        double qtyPrice = rs.getDouble("qty_price"); dto.setQtyPrice(rs.wasNull() ? null : qtyPrice);
        dto.setAmount(rs.getDouble("amount"));
        Date d = rs.getDate("expense_date"); dto.setExpenseDate(d != null ? d.toLocalDate() : null);
        return dto;
    }
}
