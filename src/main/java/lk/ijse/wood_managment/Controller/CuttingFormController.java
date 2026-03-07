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
import lk.ijse.wood_managment.Model.Cutting;
import lk.ijse.wood_managment.Dto.CuttingDTO;
import lk.ijse.wood_managment.util.UserSession;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CuttingFormController {

    @FXML
    private Button back;
    @FXML
    private TextField txtWoodId;
    @FXML
    private TextField txtQty;
    @FXML
    private TextField txtDescription;
    @FXML
    private DatePicker dpDate;
    @FXML
    private TableView<CuttingDTO> tblCutting;
    @FXML
    private TableColumn<CuttingDTO, Integer> colCuttingId;
    @FXML
    private TableColumn<CuttingDTO, Integer> colWoodId;
    @FXML
    private TableColumn<CuttingDTO, Integer> colQty;
    @FXML
    private TableColumn<CuttingDTO, LocalDate> colDate;
    @FXML
    private TableColumn<CuttingDTO, String> colDescription;

    @FXML
    private void initialize() {
        colCuttingId.setCellValueFactory(new PropertyValueFactory<>("cuttingId"));
        colWoodId.setCellValueFactory(new PropertyValueFactory<>("woodId"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("cuttingDate"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        loadAllCuttings();


        tblCutting.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtWoodId.setText(String.valueOf(newSelection.getWoodId()));
                txtQty.setText(String.valueOf(newSelection.getQty()));
                txtDescription.setText(newSelection.getDescription());
                dpDate.setValue(newSelection.getCuttingDate());
            }
        });
    }

    @FXML
    private void btnAddS(ActionEvent event) {
        try {
            if (!validateFields()) return;

            int woodId = Integer.parseInt(txtWoodId.getText());
            int qty = Integer.parseInt(txtQty.getText());
            String description = txtDescription.getText();
            LocalDate date = dpDate.getValue();

            CuttingDTO dto = new CuttingDTO(0, woodId, qty, date, description);
            if (Cutting.save(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Cutting added successfully!");
                clearFields();
                loadAllCuttings();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add cutting!");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Wood ID and Quantity must be numbers!");
        }
    }

    @FXML
    private void btnUpdate(ActionEvent event) {
        try {
            CuttingDTO selected = tblCutting.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a row to update!");
                return;
            }

            if (!validateFields()) return;

            int woodId = Integer.parseInt(txtWoodId.getText());
            int qty = Integer.parseInt(txtQty.getText());
            String description = txtDescription.getText();
            LocalDate date = dpDate.getValue();

            CuttingDTO dto = new CuttingDTO(selected.getCuttingId(), woodId, qty, date, description);
            if (Cutting.update(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Cutting updated successfully!");
                clearFields();
                loadAllCuttings();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update cutting!");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Wood ID and Quantity must be numbers!");
        }
    }

    @FXML
    private void dtnDelete(ActionEvent event) {
        try {
            CuttingDTO selected = tblCutting.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a row to delete!");
                return;
            }

            if (Cutting.delete(selected.getCuttingId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Cutting deleted successfully!");
                clearFields();
                loadAllCuttings();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete cutting!");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void btnSearch(ActionEvent event) {
        try {
            if (txtWoodId.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter Wood ID to search!");
                return;
            }

            int woodId = Integer.parseInt(txtWoodId.getText());
            CuttingDTO dto = Cutting.searchByWoodId(woodId);
            if (dto != null) {
                ObservableList<CuttingDTO> list = FXCollections.observableArrayList(dto);
                tblCutting.setItems(list);
                txtQty.setText(String.valueOf(dto.getQty()));
                txtDescription.setText(dto.getDescription());
                dpDate.setValue(dto.getCuttingDate());
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No Result", "No cutting found for this Wood ID!");
                clearFields();
                loadAllCuttings();
                tblCutting.getItems().clear();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Wood ID must be a number!");
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



    private void loadAllCuttings() {
        try {
            List<CuttingDTO> list = Cutting.getAll();
            ObservableList<CuttingDTO> obList = FXCollections.observableArrayList(list);
            tblCutting.setItems(obList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }
    private void clearFields() {
        txtWoodId.clear();
        txtQty.clear();
        txtDescription.clear();
        dpDate.setValue(null);
        tblCutting.getSelectionModel().clearSelection();
    }



    private boolean validateFields() {
        if (txtWoodId.getText().isEmpty() || txtQty.getText().isEmpty() || txtDescription.getText().isEmpty() || dpDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill all fields!");
            return false;
        }
        return true;
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
