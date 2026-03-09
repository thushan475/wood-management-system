package lk.ijse.wood_management.bo.custom;
import lk.ijse.wood_management.bo.SuperBO;
import lk.ijse.wood_management.dto.CustomerDTO;
import javafx.collections.ObservableList;
import java.sql.SQLException;
public interface CustomerBO extends SuperBO {
    ObservableList<CustomerDTO> getAllCustomers() throws SQLException;
    boolean addCustomer(CustomerDTO dto) throws SQLException;
    boolean updateCustomer(CustomerDTO dto) throws SQLException;
    boolean deleteCustomer(int customerId) throws SQLException;
    CustomerDTO searchById(int customerId) throws SQLException;
    CustomerDTO searchByName(String name) throws SQLException;
}
