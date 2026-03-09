package lk.ijse.wood_management.dao.custom.impl;

import lk.ijse.wood_management.dao.custom.BillDAO;

import lk.ijse.wood_management.db.DBConnection;
import lk.ijse.wood_management.entity.Bill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAOImpl implements BillDAO {

    public List<Bill> getAll() throws SQLException {
        List<Bill> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
             try (ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM Bill ORDER BY bill_id DESC")) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Bill> findById(int billId) throws SQLException {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM Bill WHERE bill_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, billId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public boolean save(Bill bill, Integer customBillId) throws SQLException {
        String sql = customBillId != null
                ? "INSERT INTO Bill(bill_id, order_id, amount, bill_date, description) VALUES (?, ?, ?, ?, ?)"
                : "INSERT INTO Bill(order_id, amount, bill_date, description) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            int idx = 1;
            if (customBillId != null) pst.setInt(idx++, customBillId);
            pst.setInt(idx++, bill.getOrderId());
            pst.setDouble(idx++, bill.getAmount());
            pst.setDate(idx++, Date.valueOf(bill.getBillDate()));
            pst.setString(idx, bill.getDescription() == null ? "" : bill.getDescription());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean update(Bill bill) throws SQLException {
        String sql = "UPDATE Bill SET order_id=?, amount=?, bill_date=?, description=? WHERE bill_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, bill.getOrderId());
            pst.setDouble(2, bill.getAmount());
            pst.setDate(3, Date.valueOf(bill.getBillDate()));
            pst.setString(4, bill.getDescription() == null ? "" : bill.getDescription());
            pst.setInt(5, bill.getBillId());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean delete(int billId) throws SQLException {
        String sql = "DELETE FROM Bill WHERE bill_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, billId);
            return pst.executeUpdate() > 0;
        }
    }

    public boolean existsById(int billId) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
             try (PreparedStatement pst = conn.prepareStatement("SELECT COUNT(*) FROM Bill WHERE bill_id=?")) {
            pst.setInt(1, billId);
            ResultSet rs = pst.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public List<Integer> getAllOrderIds() throws SQLException {
        List<Integer> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
             try (ResultSet rs = conn.createStatement().executeQuery("SELECT order_id FROM customer_order ORDER BY order_id DESC")) {
            while (rs.next()) list.add(rs.getInt("order_id"));
        }
        return list;
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        return new Bill(
                rs.getInt("bill_id"), rs.getInt("order_id"),
                rs.getDouble("amount"), rs.getDate("bill_date").toLocalDate(),
                rs.getString("description")
        );
    }
}
