package lk.ijse.wood_management.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lk.ijse.wood_management.dto.CustomerDTO;
import lk.ijse.wood_management.bo.custom.impl.CustomerBOImpl;
import lk.ijse.wood_management.bo.BOFactory;
import lk.ijse.wood_management.util.UserSession;

public class CustomerFormController {

    @FXML private TextField txtUserid, txtCustomerid, txtname, txtlocation, txtcontact;
    @FXML private TableView<CustomerDTO> tblCustomer;
    @FXML private TableColumn<CustomerDTO, Integer> colUserId, colCustomerId;
    @FXML private TableColumn<CustomerDTO, String> colName, colLocation, colContact;
    @FXML private Button back;

    private final CustomerBOImpl customerService = new CustomerBOImpl();
    private final ObservableList<CustomerDTO> customerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        tblCustomer.setItems(customerList);
        loadCustomers();

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                txtUserid.setText(String.valueOf(selected.getUserId()));
                txtCustomerid.setText(String.valueOf(selected.getCustomerId()));
                txtname.setText(selected.getName());
                txtlocation.setText(selected.getLocation());
                txtcontact.setText(selected.getContact());
            }
        });
    }

    @FXML private void btnAddOnAction() {
        try {
            CustomerDTO dto = buildDTO(0);
            customerService.addCustomer(dto);
            loadCustomers();
            showAlert(Alert.AlertType.INFORMATION, "Customer added successfully!");
            clearFields();
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed to add customer!");
            e.printStackTrace();
        }
    }

    @FXML private void btnUpdateOnAction() {
        CustomerDTO selected = tblCustomer.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Select a customer first!"); return; }
        try {
            CustomerDTO dto = buildDTO(selected.getCustomerId());
            customerService.updateCustomer(dto);
            loadCustomers();
            showAlert(Alert.AlertType.INFORMATION, "Customer updated successfully!");
            clearFields();
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed to update customer!");
            e.printStackTrace();
        }
    }

    @FXML private void btnDeleteOnAction(ActionEvent event) {
        CustomerDTO selected = tblCustomer.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Please select a customer first!"); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this customer?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES) return;

        try {
            customerService.deleteCustomer(selected.getCustomerId());
            loadCustomers();
            showAlert(Alert.AlertType.INFORMATION, "Customer deleted successfully!");
            clearFields();
        } catch (IllegalStateException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed to delete customer!");
            e.printStackTrace();
        }
    }

    @FXML private void btnSearchOnAction() {
        try {
            String idText   = txtCustomerid.getText().trim();
            String nameText = txtname.getText().trim();
            CustomerDTO result = null;

            if (!idText.isEmpty()) {
                result = customerService.searchById(Integer.parseInt(idText));
            } else if (!nameText.isEmpty()) {
                result = customerService.searchByName(nameText);
            } else {
                showAlert(Alert.AlertType.WARNING, "Enter Customer ID or Name!"); return;
            }

            if (result != null) {
                txtUserid.setText(String.valueOf(result.getUserId()));
                txtCustomerid.setText(String.valueOf(result.getCustomerId()));
                txtname.setText(result.getName());
                txtlocation.setText(result.getLocation());
                txtcontact.setText(result.getContact());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Customer not found!"); clearFields();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Customer ID!");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void btnBackOnAction(ActionEvent event) { navigateToDashboard(); }

    private void loadCustomers() {
        try {
            customerList.setAll(customerService.getAllCustomers());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private CustomerDTO buildDTO(int customerId) {
        return new CustomerDTO(
                Integer.parseInt(txtUserid.getText().trim()),
                customerId, txtname.getText().trim(),
                txtlocation.getText().trim(), txtcontact.getText().trim());
    }

    private void clearFields() {
        txtUserid.clear(); txtCustomerid.clear(); txtname.clear(); txtlocation.clear(); txtcontact.clear();
    }

    private void showAlert(Alert.AlertType type, String msg) { new Alert(type, msg).show(); }

    private void navigateToDashboard() {
        try {
            String role = UserSession.getUserRole();
            String path = role.equals("ADMIN") ? "/lk/ijse/wood_management/admindashboard.fxml" : "/lk/ijse/wood_management/empdashboard.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) back.getScene().getWindow();
            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle(role.equals("ADMIN") ? "Admin Dashboard" : "Employee Dashboard");
            stage.centerOnScreen(); stage.setResizable(false);
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Cannot return to dashboard!"); }
    }
}
