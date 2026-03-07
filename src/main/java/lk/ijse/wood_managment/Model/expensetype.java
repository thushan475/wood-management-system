package lk.ijse.wood_managment.Model;

import lk.ijse.wood_managment.Dto.ExpenseTypeDTO;
import lk.ijse.wood_managment.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class expensetype {

    public static boolean save(ExpenseTypeDTO dto) throws SQLException {
        String sql = "INSERT INTO expense_type (expense_type_id, expense_name) VALUES (?, ?)";
        PreparedStatement pst = DBConnection.getInstance()
                .getConnection().prepareStatement(sql);

        pst.setInt(1, dto.getExpenseId());
        pst.setString(2, dto.getExpenseName());

        return pst.executeUpdate() > 0;
    }

    public static boolean update(ExpenseTypeDTO dto) throws SQLException {
        String sql = "UPDATE expense_type SET expense_name=? WHERE expense_type_id=?";
        PreparedStatement pst = DBConnection.getInstance()
                .getConnection().prepareStatement(sql);

        pst.setString(1, dto.getExpenseName());
        pst.setInt(2, dto.getExpenseId());

        return pst.executeUpdate() > 0;
    }

    public static boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM expense_type WHERE expense_type_id=?";
        PreparedStatement pst = DBConnection.getInstance()
                .getConnection().prepareStatement(sql);

        pst.setInt(1, id);
        return pst.executeUpdate() > 0;
    }

    public static ExpenseTypeDTO search(int id) throws SQLException {
        String sql = "SELECT * FROM expense_type WHERE expense_type_id=?";
        PreparedStatement pst = DBConnection.getInstance()
                .getConnection().prepareStatement(sql);

        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return new ExpenseTypeDTO(
                    rs.getInt("expense_type_id"),
                    rs.getString("expense_name")
            );
        }
        return null;
    }

    public static List<ExpenseTypeDTO> getAll() throws SQLException {
        List<ExpenseTypeDTO> list = new ArrayList<>();

        String sql = "SELECT * FROM expense_type";
        ResultSet rs = DBConnection.getInstance()
                .getConnection()
                .prepareStatement(sql)
                .executeQuery();

        while (rs.next()) {
            list.add(new ExpenseTypeDTO(
                    rs.getInt("expense_type_id"),
                    rs.getString("expense_name")
            ));
        }
        return list;
    }
}
