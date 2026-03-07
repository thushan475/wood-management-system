package lk.ijse.wood_managment.Controller;

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
import lk.ijse.wood_managment.Model.Customer;
import lk.ijse.wood_managment.Dto.CustomerDTO;
import lk.ijse.wood_managment.util.UserSession;

public class CustomerFormController {

    @FXML private TextField txtUserid;
    @FXML private TextField txtCustomerid;
    @FXML private TextField txtname;
    @FXML private TextField txtlocation;
    @FXML private TextField txtcontact;

    @FXML private TableView<CustomerDTO> tblCustomer;
    @FXML private TableColumn<CustomerDTO, Integer> colUserId;
    @FXML private TableColumn<CustomerDTO, Integer> colCustomerId;
    @FXML private TableColumn<CustomerDTO, String> colName;
    @FXML private TableColumn<CustomerDTO, String> colLocation;
    @FXML private TableColumn<CustomerDTO, String> colContact;

    private ObservableList<CustomerDTO> customerList = FXCollections.observableArrayList();

    @FXML private Button back;

    @FXML
    public void initialize() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));

        tblCustomer.setItems(customerList);
        customerList.addAll(Customer.getAllCustomers());

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtUserid.setText(String.valueOf(newSelection.getUserId()));
                txtCustomerid.setText(String.valueOf(newSelection.getCustomerId()));
                txtname.setText(newSelection.getName());
                txtlocation.setText(newSelection.getLocation());
                txtcontact.setText(newSelection.getContact());
            }
        });
    }

    private void clearFields() {
        txtUserid.clear();
        txtCustomerid.clear();
        txtname.clear();
        txtlocation.clear();
        txtcontact.clear();
    }
    @FXML
    private void btnAddOnAction() {
        try {
            String contact = txtcontact.getText().trim();
            if (!contact.matches("\\d{10}")) {
                new Alert(Alert.AlertType.ERROR, "Contact number must be exactly 10 digits!").show();
                return;
            }

            CustomerDTO customer = new CustomerDTO(
                    Integer.parseInt(txtUserid.getText()),
                    0,
                    txtname.getText().trim(),
                    txtlocation.getText().trim(),
                    contact
            );

            if (Customer.addCustomer(customer)) {
                refreshCustomerList();
                new Alert(Alert.AlertType.INFORMATION, "Customer added successfully!").show();
                clearFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to add customer!").show();
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid User ID!").show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnUpdateOnAction() {
        try {
            CustomerDTO selected = tblCustomer.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String contact = txtcontact.getText().trim();
                if (!contact.matches("\\d{10}")) {
                    new Alert(Alert.AlertType.ERROR, "Contact number must be exactly 10 digits!").show();
                    return;
                }

                selected.setUserId(Integer.parseInt(txtUserid.getText()));
                selected.setName(txtname.getText().trim());
                selected.setLocation(txtlocation.getText().trim());
                selected.setContact(contact);

                if (Customer.updateCustomer(selected)) {
                    refreshCustomerList();
                    new Alert(Alert.AlertType.INFORMATION, "Customer updated successfully!").show();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update customer!").show();
                }
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid User ID!").show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnDeleteOnAction(ActionEvent event) {
        CustomerDTO selectedCustomer = tblCustomer.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a customer first!").show();
            return;
        }

        int customerId = selectedCustomer.getCustomerId();

        if (Customer.hasOrders(customerId)) {
            new Alert(Alert.AlertType.ERROR, "Cannot delete! This customer has orders.").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this customer?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean deleted = Customer.deleteCustomer(customerId);
            if (deleted) {
                refreshCustomerList();
                new Alert(Alert.AlertType.INFORMATION, "Customer deleted successfully!").show();
                clearFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to delete customer!").show();
            }
        }
    }

    @FXML
    private void btnSearchOnAction() {
        try {
            String idText = txtCustomerid.getText().trim();
            String nameText = txtname.getText().trim();

            CustomerDTO result = null;

            if (!idText.isEmpty()) {
                int customerId = Integer.parseInt(idText);
                result = Customer.searchCustomer(customerId);
            } else if (!nameText.isEmpty()) {
                result = Customer.searchCustomerByName(nameText);
            } else {
                new Alert(Alert.AlertType.WARNING, "Please enter Customer ID or Name!").show();
                return;
            }

            if (result != null) {
                txtUserid.setText(String.valueOf(result.getUserId()));
                txtCustomerid.setText(String.valueOf(result.getCustomerId()));
                txtname.setText(result.getName());
                txtlocation.setText(result.getLocation());
                txtcontact.setText(result.getContact());
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Customer not found!").show();
                clearFields();
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid Customer ID!").show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnBackOnAction(ActionEvent event) {
        try {
            String role = UserSession.getUserRole();
            String fxmlPath = role.equals("ADMIN")
                    ? "/lk/ijse/wood_managment/admindashboard.fxml"
                    : "/lk/ijse/wood_managment/empdashboard.fxml";

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));

            Stage stage = (Stage) back.getScene().getWindow();
            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle(role.equals("ADMIN") ? "Admin Dashboard" : "Employee Dashboard");
            stage.centerOnScreen();
            stage.setResizable(false);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Cannot return to dashboard!").show();
            e.printStackTrace();
        }
    }



    private void refreshCustomerList() {
        customerList.clear();
        customerList.addAll(Customer.getAllCustomers());
    }
}
