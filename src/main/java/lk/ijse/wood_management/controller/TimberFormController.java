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
import lk.ijse.wood_management.entity.Timber;
import lk.ijse.wood_management.bo.custom.impl.TimberBOImpl;
import lk.ijse.wood_management.util.UserSession;

public class TimberFormController {

    @FXML private TextField txtTimberId, txtCuttingId, txtQuantity, txtWidth, txtLength, txtSpecies;
    @FXML private TableView<Timber> tblTimber;
    @FXML private TableColumn<Timber, Integer> colTimberId, colCuttingId;
    @FXML private TableColumn<Timber, Double> colQuantity, colWidth, colLength;
    @FXML private TableColumn<Timber, String> colSpecies;
    @FXML private Button back;

    private final TimberBOImpl timberService = new TimberBOImpl();
    private final ObservableList<Timber> list = FXCollections.observableArrayList();

    public void initialize() {
        colTimberId.setCellValueFactory(new PropertyValueFactory<>("timberId"));
        colCuttingId.setCellValueFactory(new PropertyValueFactory<>("cuttingId"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colWidth.setCellValueFactory(new PropertyValueFactory<>("width"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("length"));
        colSpecies.setCellValueFactory(new PropertyValueFactory<>("species"));
        tblTimber.setItems(list);
        loadTable();
        tblTimber.getSelectionModel().selectedItemProperty().addListener((obs, old, t) -> { if (t != null) fillFields(t); });
    }

    @FXML void btnAdd(ActionEvent event) {
        if (anyFieldEmpty()) { showAlert(Alert.AlertType.WARNING, "Please fill all fields before adding timber."); return; }
        try {
            Timber t = buildEntity();
            boolean isNew = timberService.addTimber(t);
            showAlert(Alert.AlertType.INFORMATION, isNew ? "New Wood Size Added Successfully." : "Same Wood Found.\nQuantity Updated Successfully.");
            loadTable(); clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Please enter valid numbers.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Cannot add timber: " + e.getMessage());
        }
    }

    @FXML void btnUpdate(ActionEvent event) {
        try {
            timberService.updateTimber(buildEntity());
            showAlert(Alert.AlertType.INFORMATION, "Updated Successfully");
            loadTable(); clearFields();
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Update Failed: " + e.getMessage()); }
    }

    @FXML void btnDelete(ActionEvent event) {
        try {
            timberService.deleteTimber(Integer.parseInt(txtTimberId.getText()));
            showAlert(Alert.AlertType.INFORMATION, "Deleted Successfully");
            loadTable(); clearFields();
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Delete Failed: " + e.getMessage()); }
    }

    @FXML void btnSearch(ActionEvent event) {
        try {
            Integer tId = txtTimberId.getText().isEmpty() ? null : Integer.parseInt(txtTimberId.getText());
            Double w    = txtWidth.getText().isEmpty()    ? null : Double.parseDouble(txtWidth.getText());
            Double l    = txtLength.getText().isEmpty()   ? null : Double.parseDouble(txtLength.getText());
            Double q    = txtQuantity.getText().isEmpty() ? null : Double.parseDouble(txtQuantity.getText());
            String sp   = txtSpecies.getText().isEmpty()  ? null : txtSpecies.getText();
            list.setAll(timberService.searchTimber(tId, w, l, q, sp));
            if (list.isEmpty()) showAlert(Alert.AlertType.WARNING, "No matching records found.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Please enter valid numbers.");
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Search Failed: " + e.getMessage()); }
    }

    @FXML private void btnback(ActionEvent event) { navigateToDashboard(); }
    @FXML void btnClear(ActionEvent event) { clearFields(); }

    private void loadTable() {
        try { list.setAll(timberService.getAllTimber()); } catch (Exception e) { e.printStackTrace(); }
    }

    private Timber buildEntity() {
        return new Timber(
                Integer.parseInt(txtTimberId.getText()), Integer.parseInt(txtCuttingId.getText()),
                Double.parseDouble(txtQuantity.getText()), Double.parseDouble(txtWidth.getText()),
                Double.parseDouble(txtLength.getText()), txtSpecies.getText());
    }

    private boolean anyFieldEmpty() {
        return txtTimberId.getText().isEmpty() || txtCuttingId.getText().isEmpty() ||
                txtQuantity.getText().isEmpty() || txtWidth.getText().isEmpty() ||
                txtLength.getText().isEmpty() || txtSpecies.getText().isEmpty();
    }

    private void fillFields(Timber t) {
        txtTimberId.setText(String.valueOf(t.getTimberId()));
        txtCuttingId.setText(String.valueOf(t.getCuttingId()));
        txtQuantity.setText(String.valueOf(t.getQuantity()));
        txtWidth.setText(String.valueOf(t.getWidth()));
        txtLength.setText(String.valueOf(t.getLength()));
        txtSpecies.setText(t.getSpecies());
    }

    private void clearFields() {
        txtTimberId.clear(); txtCuttingId.clear(); txtQuantity.clear();
        txtWidth.clear(); txtLength.clear(); txtSpecies.clear();
        tblTimber.getSelectionModel().clearSelection();
    }

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
