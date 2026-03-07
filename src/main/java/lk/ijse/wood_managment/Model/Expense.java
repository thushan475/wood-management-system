package lk.ijse.wood_managment.Model;

import lk.ijse.wood_managment.Dto.ExpenseDTO;
import lk.ijse.wood_managment.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Expense {


    public boolean addExpense(ExpenseDTO e) throws SQLException {
        String sql = """
            INSERT INTO expense
            (wood_id, expense_type, description, species,
             length, width, qty_price, amount, expense_date)
            VALUES (?,?,?,?,?,?,?,?,?)
        """;

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {


            if (e.getWoodId() != null) {
                pst.setInt(1, e.getWoodId());
            } else {
                pst.setNull(1, Types.INTEGER);
            }

            pst.setString(2, e.getExpenseType());
            pst.setString(3, e.getDescription());


            if (e.getSpecies() != null) {
                pst.setString(4, e.getSpecies());
            } else {
                pst.setNull(4, Types.VARCHAR);
            }


            if (e.getLength() != null) {
                pst.setDouble(5, e.getLength());
            } else {
                pst.setNull(5, Types.DOUBLE);
            }


            if (e.getWidth() != null) {
                pst.setDouble(6, e.getWidth());
            } else {
                pst.setNull(6, Types.DOUBLE);
            }


            if (e.getQtyPrice() != null) {
                pst.setDouble(7, e.getQtyPrice());
            } else {
                pst.setNull(7, Types.DOUBLE);
            }

            pst.setDouble(8, e.getAmount());
            pst.setDate(9, Date.valueOf(e.getExpenseDate()));

            return pst.executeUpdate() > 0;
        }
    }


    public boolean updateExpense(ExpenseDTO e) throws SQLException {
        String sql = """
            UPDATE expense SET
            expense_type=?, description=?, species=?,
            length=?, width=?, qty_price=?, amount=?, expense_date=?
            WHERE expense_id=?
        """;

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, e.getExpenseType());
            pst.setString(2, e.getDescription());

            if (e.getSpecies() != null) {
                pst.setString(3, e.getSpecies());
            } else {
                pst.setNull(3, Types.VARCHAR);
            }

            if (e.getLength() != null) {
                pst.setDouble(4, e.getLength());
            } else {
                pst.setNull(4, Types.DOUBLE);
            }

            if (e.getWidth() != null) {
                pst.setDouble(5, e.getWidth());
            } else {
                pst.setNull(5, Types.DOUBLE);
            }

            if (e.getQtyPrice() != null) {
                pst.setDouble(6, e.getQtyPrice());
            } else {
                pst.setNull(6, Types.DOUBLE);
            }

            pst.setDouble(7, e.getAmount());
            pst.setDate(8, Date.valueOf(e.getExpenseDate()));
            pst.setInt(9, e.getExpenseId());

            return pst.executeUpdate() > 0;
        }
    }


    public boolean deleteExpense(int expenseId) throws SQLException {
        String sql = "DELETE FROM expense WHERE expense_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, expenseId);
            return pst.executeUpdate() > 0;
        }
    }


    public ExpenseDTO searchExpense(int id) throws SQLException {
        String sql = "SELECT * FROM expense WHERE expense_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? map(rs) : null;
        }
    }


    public List<ExpenseDTO> getAllExpenses() throws SQLException {
        List<ExpenseDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM expense ORDER BY expense_date DESC, expense_id DESC";

        try (Connection con = DBConnection.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }


    private ExpenseDTO map(ResultSet rs) throws SQLException {
        ExpenseDTO dto = new ExpenseDTO();

        dto.setExpenseId(rs.getInt("expense_id"));


        int woodId = rs.getInt("wood_id");
        dto.setWoodId(rs.wasNull() ? null : woodId);

        dto.setExpenseType(rs.getString("expense_type"));
        dto.setDescription(rs.getString("description"));
        dto.setSpecies(rs.getString("species"));


        double length = rs.getDouble("length");
        dto.setLength(rs.wasNull() ? null : length);


        double width = rs.getDouble("width");
        dto.setWidth(rs.wasNull() ? null : width);


        double qtyPrice = rs.getDouble("qty_price");
        dto.setQtyPrice(rs.wasNull() ? null : qtyPrice);

        dto.setAmount(rs.getDouble("amount"));

        Date expenseDate = rs.getDate("expense_date");
        dto.setExpenseDate(expenseDate != null ? expenseDate.toLocalDate() : null);

        return dto;
    }
}