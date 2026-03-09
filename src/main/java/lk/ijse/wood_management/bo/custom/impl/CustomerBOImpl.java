package lk.ijse.wood_management.bo.custom.impl;

import lk.ijse.wood_management.bo.custom.CustomerBO;

import javafx.collections.ObservableList;
import lk.ijse.wood_management.dto.CustomerDTO;
import lk.ijse.wood_management.dao.custom.impl.CustomerDAOImpl;

import java.sql.SQLException;

public class CustomerBOImpl implements CustomerBO {

    private final CustomerDAOImpl customerRepo = new CustomerDAOImpl();

    public ObservableList<CustomerDTO> getAllCustomers() throws SQLException {
        return customerRepo.getAll();
    }

    public boolean addCustomer(CustomerDTO dto) throws SQLException {
        if (!dto.getContact().matches("\\d{10}")) {
            throw new IllegalArgumentException("Contact number must be exactly 10 digits!");
        }
        return customerRepo.save(dto);
    }

    public boolean updateCustomer(CustomerDTO dto) throws SQLException {
        if (!dto.getContact().matches("\\d{10}")) {
            throw new IllegalArgumentException("Contact number must be exactly 10 digits!");
        }
        return customerRepo.update(dto);
    }

    public boolean deleteCustomer(int customerId) throws SQLException {
        if (customerRepo.hasOrders(customerId)) {
            throw new IllegalStateException("Cannot delete! This customer has orders.");
        }
        return customerRepo.delete(customerId);
    }

    public CustomerDTO searchById(int customerId) throws SQLException {
        return customerRepo.findById(customerId);
    }

    public CustomerDTO searchByName(String name) throws SQLException {
        return customerRepo.findByName(name);
    }
}
