package lk.ijse.wood_management.bo.custom;

import lk.ijse.wood_management.bo.SuperBO;
import lk.ijse.wood_management.dto.OrderDTO;
import net.sf.jasperreports.engine.JasperPrint;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface OrderBO extends SuperBO {

    List<OrderDTO> getAllOrders() throws SQLException;

    List<OrderDTO> getOrderById(int orderId) throws SQLException;

    List<Integer> getAllCustomerIds() throws SQLException;

    int submitOrder(int customerId, LocalDate date, String description, List<OrderDTO> items) throws SQLException;

    void deleteOrder(int orderId) throws SQLException;

    JasperPrint generateInvoice(int orderId) throws Exception;
}