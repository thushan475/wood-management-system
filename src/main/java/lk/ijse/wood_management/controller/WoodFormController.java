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
import lk.ijse.wood_management.dto.WoodDTO;
import lk.ijse.wood_management.entity.Wood;
import lk.ijse.wood_management.bo.custom.impl.SupplierBOImpl;
import lk.ijse.wood_management.bo.custom.impl.WoodBOImpl;
import lk.ijse.wood_management.util.UserSession;

import java.sql.Date;

public class WoodFormController {

    @FXML private ComboBox<Integer> cmbSupId;
    @FXML private TextField txtSpecies, txtLength, txtWidth, txtUnitPrice;
    @FXML private DatePicker dpPurchaseDate;
    @FXML private TableView<Wood> tblWood;
    @FXML private TableColumn<Wood, Integer> colWoodId, colSupId;
    @FXML private TableColumn<Wood, String> colSpecies;
    @FXML private TableColumn<Wood, Date> colPurchaseDate;
    @FXML private TableColumn<Wood, Double> colLength, colWidth, colUnitPrice;
    @FXML private Button btnBack;

    private final WoodBOImpl woodService = new WoodBOImpl();
    private final SupplierBOImpl supplierService = new SupplierBOImpl();
    private final ObservableList<Wood> woodList = FXCollections.observableArrayList();

    @FXML public void initialize() {
        colWoodId.setCellValueFactory(new PropertyValueFactory<>("woodId"));
        colSupId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colSpecies.setCellValueFactory(new PropertyValueFactory<>("species"));
        colPurchaseDate.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("length"));
        colWidth.setCellValueFactory(new PropertyValueFactory<>("width"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblWood.setItems(woodList);
        loadSupplierIds();
        loadAllWood();
        tblWood.getSelectionModel().selectedItemProperty().addListener((obs, old, wood) -> {
            if (wood != null) {
                cmbSupId.setValue(wood.getSupplierId());
                txtSpecies.setText(wood.getSpecies());
                txtLength.setText(String.valueOf(wood.getLength()));
                txtWidth.setText(String.valueOf(wood.getWidth()));
                txtUnitPrice.setText(String.valueOf(wood.getUnitPrice()));
                dpPurchaseDate.setValue(wood.getPurchaseDate() != null ? wood.getPurchaseDate().toLocalDate() : null);
            }
        });
    }

    @FXML private void addWood(ActionEvent event) {
        if (!validateFields()) return;
        try {
            woodService.addWoodWithExpense(buildDTO(-1));
            showAlert(Alert.AlertType.INFORMATION, "Success", "Wood and expense added successfully!");
            clearFields(); loadAllWood();
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }

    @FXML private void updateWood(ActionEvent event) {
        Wood selected = tblWood.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Selection Error", "Select a row first"); return; }
        if (!validateFields()) return;
        try {
            woodService.updateWood(buildDTO(selected.getWoodId()));
            showAlert(Alert.AlertType.INFORMATION, "Success", "Update Successful");
            clearFields(); loadAllWood();
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }

    @FXML private void deleteWood(ActionEvent event) {
        Wood selected = tblWood.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Selection Error", "Select a row first"); return; }
        try {
            if (woodService.deleteWood(selected.getWoodId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Delete Successful");
                clearFields(); loadAllWood();
            } else showAlert(Alert.AlertType.ERROR, "Error", "Delete Failed");
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }

    @FXML private void searchWood(ActionEvent event) {
        try {
            String sp = txtSpecies.getText().isEmpty() ? null : txtSpecies.getText();
            Integer supId = cmbSupId.getValue();
            woodList.setAll(woodService.searchWood(sp, supId));
            if (woodList.isEmpty()) showAlert(Alert.AlertType.INFORMATION, "No Data", "No Data Found");
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }

    @FXML private void backToDashboard(ActionEvent event) { navigateToDashboard(); }

    private void loadSupplierIds() {
        try { cmbSupId.setItems(FXCollections.observableArrayList(supplierService.getAllSupplierIds())); }
        catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Cannot load supplier IDs"); }
    }

    private void loadAllWood() {
        try { woodList.setAll(woodService.getAllWood()); } catch (Exception e) { e.printStackTrace(); }
    }

    private WoodDTO buildDTO(int woodId) {
        WoodDTO dto = new WoodDTO(woodId, cmbSupId.getValue(), txtSpecies.getText(),
                Double.parseDouble(txtLength.getText()), Double.parseDouble(txtWidth.getText()),
                dpPurchaseDate.getValue() != null ? Date.valueOf(dpPurchaseDate.getValue()) : null,
                Double.parseDouble(txtUnitPrice.getText()));
        return dto;
    }

    private boolean validateFields() {
        if (cmbSupId.getValue() == null || txtSpecies.getText().isEmpty() ||
                txtLength.getText().isEmpty() || txtWidth.getText().isEmpty() ||
                txtUnitPrice.getText().isEmpty() || dpPurchaseDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill all fields"); return false;
        }
        return true;
    }

    private void clearFields() {
        cmbSupId.setValue(null); txtSpecies.clear(); txtLength.clear();
        txtWidth.clear(); txtUnitPrice.clear(); dpPurchaseDate.setValue(null);
        tblWood.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(message); a.showAndWait();
    }

    private void navigateToDashboard() {
        try {
            String role = UserSession.getUserRole();
            String path = role.equals("ADMIN") ? "/lk/ijse/wood_management/admindashboard.fxml" : "/lk/ijse/wood_management/empdashboard.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle(role.equals("ADMIN") ? "Admin Dashboard" : "Employee Dashboard");
            stage.centerOnScreen(); stage.setResizable(false);
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Cannot load dashboard"); }
    }
}
