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
import lk.ijse.wood_management.dto.SupplierDTO;
import lk.ijse.wood_management.bo.custom.impl.SupplierBOImpl;
import lk.ijse.wood_management.util.UserSession;

public class SupplierFormController {

    @FXML private Button back;
    @FXML private TextField txtId, txtName, txtContact, txtdescription;
    @FXML private TableView<SupplierDTO> tblSupplier;
    @FXML private TableColumn<SupplierDTO, Integer> colId;
    @FXML private TableColumn<SupplierDTO, String> colName, colContact, coldescription;

    private final SupplierBOImpl supplierService = new SupplierBOImpl();
    private final ObservableList<SupplierDTO> supplierList = FXCollections.observableArrayList();

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        coldescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tblSupplier.setItems(supplierList);
        loadSuppliers();

        tblSupplier.getSelectionModel().selectedItemProperty().addListener((obs, old, s) -> {
            if (s != null) {
                txtId.setText(String.valueOf(s.getSupplierId()));
                txtName.setText(s.getName()); txtContact.setText(s.getContactNumber()); txtdescription.setText(s.getDescription());
            }
        });
    }

    @FXML void addSupplier(ActionEvent event) {
        try {
            supplierService.addSupplier(new SupplierDTO(0, txtName.getText(), txtContact.getText(), txtdescription.getText()));
            loadSuppliers(); clearFields();
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, e.getMessage()); }
    }

    @FXML void updateSupplier(ActionEvent event) {
        SupplierDTO s = tblSupplier.getSelectionModel().getSelectedItem();
        if (s == null) return;
        try {
            supplierService.updateSupplier(new SupplierDTO(s.getSupplierId(), txtName.getText(), txtContact.getText(), txtdescription.getText()));
            loadSuppliers(); clearFields();
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, e.getMessage()); }
    }

    @FXML void deleteSupplier(ActionEvent event) {
        SupplierDTO s = tblSupplier.getSelectionModel().getSelectedItem();
        if (s == null) return;
        try {
            supplierService.deleteSupplier(s.getSupplierId());
            loadSuppliers(); clearFields();
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, e.getMessage()); }
    }

    @FXML void searchSupplier(ActionEvent event) {
        try {
            supplierList.setAll(supplierService.searchByName(txtName.getText()));
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, e.getMessage()); }
    }

    @FXML private void backAction(ActionEvent event) { navigateToDashboard(); }

    private void loadSuppliers() {
        try { supplierList.setAll(supplierService.getAllSuppliers()); } catch (Exception e) { e.printStackTrace(); }
    }

    private void clearFields() { txtId.clear(); txtName.clear(); txtContact.clear(); txtdescription.clear(); }
    private void showAlert(Alert.AlertType t, String m) { new Alert(t, m).show(); }

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
