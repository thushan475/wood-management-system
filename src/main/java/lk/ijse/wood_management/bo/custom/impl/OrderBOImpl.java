package lk.ijse.wood_management.bo.custom.impl;

import lk.ijse.wood_management.bo.custom.OrderBO;
import lk.ijse.wood_management.db.DBConnection;
import lk.ijse.wood_management.dto.OrderDTO;
import lk.ijse.wood_management.dao.custom.impl.OrderDAOImpl;
import lk.ijse.wood_management.dao.custom.impl.TimberDAOImpl;
import net.sf.jasperreports.engine.*;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class OrderBOImpl implements OrderBO {

    private final OrderDAOImpl orderRepo = new OrderDAOImpl();
    private final TimberDAOImpl timberRepo = new TimberDAOImpl();

    @Override
    public List<OrderDTO> getAllOrders() throws SQLException {
        return orderRepo.getAll();
    }

    @Override
    public List<OrderDTO> getOrderById(int orderId) throws SQLException {
        return orderRepo.findById(orderId);
    }

    @Override
    public List<Integer> getAllCustomerIds() throws SQLException {
        return orderRepo.getAllCustomerIds();
    }

    @Override
    public int submitOrder(int customerId, LocalDate date, String description, List<OrderDTO> items) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);

            double totalAmount = items.stream().mapToDouble(OrderDTO::getTotalAmount).sum();
            int orderId = orderRepo.saveOrder(conn, customerId, date, description, totalAmount);

            Map<String, OrderDTO> consolidated = new LinkedHashMap<>();
            for (OrderDTO item : items) {
                BigDecimal w = toBigDecimal(item.getWidth());
                BigDecimal l = toBigDecimal(item.getLength());
                String key = item.getTimberId() + "-" + w + "-" + l + "-" + item.getSpecies();
                if (consolidated.containsKey(key)) {
                    OrderDTO ex = consolidated.get(key);
                    ex.setQuantity(ex.getQuantity() + item.getQuantity());
                    ex.setTotalAmount(ex.getTotalAmount() + item.getTotalAmount());
                } else {
                    consolidated.put(key, new OrderDTO(
                            item.getOrderId(), item.getCustomerId(), item.getOrderDate(),
                            item.getTotalAmount(), item.getDescription(), item.getTimberId(),
                            item.getWidth(), item.getLength(), item.getSpecies(), item.getQuantity()));
                }
            }

            for (OrderDTO item : consolidated.values()) {
                BigDecimal w = toBigDecimal(item.getWidth());
                BigDecimal l = toBigDecimal(item.getLength());
                double stock = timberRepo.getAvailableStock(item.getTimberId(), w, l, item.getSpecies());
                if (stock < item.getQuantity()) {
                    throw new SQLException("Insufficient stock for Timber " + item.getTimberId());
                }
                orderRepo.saveOrderItem(conn, orderId, item, w, l);
                timberRepo.reduceStock(conn, item.getTimberId(), w, l, item.getSpecies(), item.getQuantity());
            }

            conn.commit();
            return orderId;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public void deleteOrder(int orderId) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);
            orderRepo.restoreStockForOrder(conn, orderId);
            orderRepo.deleteOrderItems(conn, orderId);
            orderRepo.deleteOrder(conn, orderId);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public JasperPrint generateInvoice(int orderId) throws Exception {
        Connection conn = DBConnection.getInstance().getConnection();
        InputStream stream = getClass().getResourceAsStream("/reports/billInvoice.jrxml");
        if (stream == null) {
            throw new Exception("Report file not found: /reports/billInvoice.jrxml");
        }
        JasperReport report = JasperCompileManager.compileReport(stream);
        Map<String, Object> params = new HashMap<>();
        params.put("ORDER_ID", orderId);
        return JasperFillManager.fillReport(report, params, conn);
    }

    private BigDecimal toBigDecimal(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP);
    }
}