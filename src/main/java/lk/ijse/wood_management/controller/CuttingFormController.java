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
import lk.ijse.wood_management.dto.CuttingDTO;
import lk.ijse.wood_management.bo.custom.impl.CuttingBOImpl;
import lk.ijse.wood_management.util.UserSession;

import java.time.LocalDate;
import java.util.List;

public class CuttingFormController {

    @FXML private Button back;
    @FXML private TextField txtWoodId, txtQty, txtDescription;
    @FXML private DatePicker dpDate;
    @FXML private TableView<CuttingDTO> tblCutting;
    @FXML private TableColumn<CuttingDTO, Integer> colCuttingId, colWoodId, colQty;
    @FXML private TableColumn<CuttingDTO, LocalDate> colDate;
    @FXML private TableColumn<CuttingDTO, String> colDescription;

    private final CuttingBOImpl cuttingService = new CuttingBOImpl();

    @FXML
    private void initialize() {
        colCuttingId.setCellValueFactory(new PropertyValueFactory<>("cuttingId"));
        colWoodId.setCellValueFactory(new PropertyValueFactory<>("woodId"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("cuttingDate"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        loadAllCuttings();

        tblCutting.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                txtWoodId.setText(String.valueOf(selected.getWoodId()));
                txtQty.setText(String.valueOf(selected.getQty()));
                txtDescription.setText(selected.getDescription());
                dpDate.setValue(selected.getCuttingDate());
            }
        });
    }

    @FXML private void btnAddS(ActionEvent event) {
        if (!validateFields()) return;
        try {
            CuttingDTO dto = new CuttingDTO(0, Integer.parseInt(txtWoodId.getText()),
                    Integer.parseInt(txtQty.getText()), dpDate.getValue(), txtDescription.getText());
            if (cuttingService.addCutting(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Cutting added successfully!");
                clearFields(); loadAllCuttings();
            } else showAlert(Alert.AlertType.ERROR, "Error", "Failed to add cutting!");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Wood ID and Quantity must be numbers!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML private void btnUpdate(ActionEvent event) {
        CuttingDTO selected = tblCutting.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a row to update!"); return; }
        if (!validateFields()) return;
        try {
            CuttingDTO dto = new CuttingDTO(selected.getCuttingId(), Integer.parseInt(txtWoodId.getText()),
                    Integer.parseInt(txtQty.getText()), dpDate.getValue(), txtDescription.getText());
            if (cuttingService.updateCutting(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Cutting updated successfully!");
                clearFields(); loadAllCuttings();
            } else showAlert(Alert.AlertType.ERROR, "Error", "Failed to update cutting!");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Wood ID and Quantity must be numbers!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML private void dtnDelete(ActionEvent event) {
        CuttingDTO selected = tblCutting.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a row to delete!"); return; }
        try {
            if (cuttingService.deleteCutting(selected.getCuttingId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Cutting deleted successfully!");
                clearFields(); loadAllCuttings();
            } else showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete cutting!");
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage()); }
    }

    @FXML private void btnSearch(ActionEvent event) {
        if (txtWoodId.getText().isEmpty()) { showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter Wood ID to search!"); return; }
        try {
            CuttingDTO dto = cuttingService.findByWoodId(Integer.parseInt(txtWoodId.getText()));
            if (dto != null) {
                tblCutting.setItems(FXCollections.observableArrayList(dto));
                txtQty.setText(String.valueOf(dto.getQty()));
                txtDescription.setText(dto.getDescription());
                dpDate.setValue(dto.getCuttingDate());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No Result", "No cutting found for this Wood ID!");
                tblCutting.getItems().clear();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Wood ID must be a number!");
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage()); }
    }

    @FXML private void btnBackOnAction(ActionEvent event) { navigateToDashboard(); }

    private void loadAllCuttings() {
        try {
            List<CuttingDTO> list = cuttingService.getAll();
            tblCutting.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage()); }
    }

    private boolean validateFields() {
        if (txtWoodId.getText().isEmpty() || txtQty.getText().isEmpty() || txtDescription.getText().isEmpty() || dpDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill all fields!"); return false;
        }
        return true;
    }

    private void clearFields() {
        txtWoodId.clear(); txtQty.clear(); txtDescription.clear();
        dpDate.setValue(null); tblCutting.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    private void navigateToDashboard() {
        try {
            String role = UserSession.getUserRole();
            String path = role.equals("ADMIN") ? "/lk/ijse/wood_management/admindashboard.fxml" : "/lk/ijse/wood_management/empdashboard.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) back.getScene().getWindow();
            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle(role.equals("ADMIN") ? "Admin Dashboard" : "Employee Dashboard");
            stage.centerOnScreen(); stage.setResizable(false);
        } catch (Exception e) { new Alert(Alert.AlertType.ERROR, "Cannot return to dashboard!").show(); }
    }
}
