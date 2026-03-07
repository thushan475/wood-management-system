package lk.ijse.wood_managment.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.wood_managment.Dto.CustomerDTO;
import lk.ijse.wood_managment.db.DBConnection;

import java.sql.*;

public class Customer {

    public static ObservableList<CustomerDTO> getAllCustomers() {
        ObservableList<CustomerDTO> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customer";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CustomerDTO customer = new CustomerDTO(
                        rs.getInt("user_id"),
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("contact_number")
                );
                list.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addCustomer(CustomerDTO customer) {
        String sql = "INSERT INTO customer(user_id, name, contact_number, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, customer.getUserId());
            pst.setString(2, customer.getName());
            pst.setString(3, customer.getContact());
            pst.setString(4, customer.getLocation());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateCustomer(CustomerDTO customer) {
        String sql = "UPDATE customer SET user_id=?, name=?, contact_number=?, address=? WHERE customer_id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, customer.getUserId());
            pst.setString(2, customer.getName());
            pst.setString(3, customer.getContact());
            pst.setString(4, customer.getLocation());
            pst.setInt(5, customer.getCustomerId());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customer WHERE customer_id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, customerId);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static CustomerDTO searchCustomer(int customerId) {
        String sql = "SELECT * FROM customer WHERE customer_id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new CustomerDTO(
                        rs.getInt("user_id"),
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("contact_number")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CustomerDTO searchCustomerByName(String name) {
        String sql = "SELECT * FROM customer WHERE name=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new CustomerDTO(
                        rs.getInt("user_id"),
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("contact_number")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean hasOrders(int customerId) {
        String sql = "SELECT COUNT(*) FROM customer_order WHERE customer_id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
