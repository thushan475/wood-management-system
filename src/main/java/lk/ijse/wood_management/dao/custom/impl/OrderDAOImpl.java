package lk.ijse.wood_management.dao.custom.impl;

import lk.ijse.wood_management.dao.custom.OrderDAO;
import lk.ijse.wood_management.db.DBConnection;
import lk.ijse.wood_management.dto.OrderDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    public List<OrderDTO> getAll() throws SQLException {
        List<OrderDTO> list = new ArrayList<>();
        String sql = "SELECT co.order_id, co.customer_id, co.order_date, co.description, " +
                "coi.timber_id, coi.width, coi.length, coi.species, coi.quantity, coi.amount " +
                "FROM customer_order co JOIN customer_order_items coi ON co.order_id = coi.order_id " +
                "ORDER BY co.order_id DESC";
        Connection conn = DBConnection.getInstance().getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<OrderDTO> findById(int orderId) throws SQLException {
        List<OrderDTO> list = new ArrayList<>();
        String orderSql = "SELECT * FROM customer_order WHERE order_id=?";
        String itemSql  = "SELECT * FROM customer_order_items WHERE order_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement orderPst = conn.prepareStatement(orderSql);
             PreparedStatement itemPst  = conn.prepareStatement(itemSql)) {
            orderPst.setInt(1, orderId);
            ResultSet orderRs = orderPst.executeQuery();
            if (orderRs.next()) {
                itemPst.setInt(1, orderId);
                ResultSet itemRs = itemPst.executeQuery();
                while (itemRs.next()) {
                    list.add(new OrderDTO(
                            orderId, orderRs.getInt("customer_id"),
                            orderRs.getDate("order_date").toLocalDate(),
                            itemRs.getDouble("amount"),
                            orderRs.getString("description"),
                            itemRs.getInt("timber_id"),
                            itemRs.getDouble("width"), itemRs.getDouble("length"),
                            itemRs.getString("species"), itemRs.getDouble("quantity")
                    ));
                }
            }
        }
        return list;
    }

    public int saveOrder(Connection conn, int customerId, java.time.LocalDate date, String description, double totalAmount) throws SQLException {
        String sql = "INSERT INTO customer_order (customer_id, order_date, description, total_amount) VALUES (?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, customerId);
            pst.setDate(2, Date.valueOf(date));
            pst.setString(3, description);
            pst.setDouble(4, totalAmount);
            pst.executeUpdate();
            ResultSet keys = pst.getGeneratedKeys();
            return keys.next() ? keys.getInt(1) : -1;
        }
    }

    public void saveOrderItem(Connection conn, int orderId, OrderDTO item, BigDecimal width, BigDecimal length) throws SQLException {
        String sql = "INSERT INTO customer_order_items (order_id,timber_id,width,length,species,quantity,amount) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, orderId);
            pst.setInt(2, item.getTimberId());
            pst.setBigDecimal(3, width.setScale(2, RoundingMode.HALF_UP));
            pst.setBigDecimal(4, length.setScale(2, RoundingMode.HALF_UP));
            pst.setString(5, item.getSpecies());
            pst.setDouble(6, item.getQuantity());
            pst.setDouble(7, item.getTotalAmount());
            pst.executeUpdate();
        }
    }

    public void deleteOrderItems(Connection conn, int orderId) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement("DELETE FROM customer_order_items WHERE order_id=?")) {
            pst.setInt(1, orderId);
            pst.executeUpdate();
        }
    }

    public void deleteOrder(Connection conn, int orderId) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement("DELETE FROM customer_order WHERE order_id=?")) {
            pst.setInt(1, orderId);
            pst.executeUpdate();
        }
    }

    public void restoreStockForOrder(Connection conn, int orderId) throws SQLException {
        String sql = "UPDATE timber t JOIN customer_order_items oi ON t.timber_id=oi.timber_id " +
                "AND t.width=oi.width AND t.length=oi.length AND t.species=oi.species " +
                "SET t.quantity=t.quantity+oi.quantity WHERE oi.order_id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, orderId);
            pst.executeUpdate();
        }
    }

    public List<Integer> getAllCustomerIds() throws SQLException {
        List<Integer> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT customer_id FROM customer ORDER BY customer_id")) {
            while (rs.next()) list.add(rs.getInt("customer_id"));
        }
        return list;
    }

    private OrderDTO mapRow(ResultSet rs) throws SQLException {
        return new OrderDTO(
                rs.getInt("order_id"), rs.getInt("customer_id"),
                rs.getDate("order_date").toLocalDate(), rs.getDouble("amount"),
                rs.getString("description"), rs.getInt("timber_id"),
                rs.getDouble("width"), rs.getDouble("length"),
                rs.getString("species"), rs.getDouble("quantity")
        );
    }
}
