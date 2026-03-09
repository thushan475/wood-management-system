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
import lk.ijse.wood_management.db.DBConnection;
import lk.ijse.wood_management.entity.Bill;
import lk.ijse.wood_management.bo.custom.impl.BillBOImpl;
import lk.ijse.wood_management.util.UserSession;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class BillFormController {

    @FXML private TableView<Bill> tblBill;
    @FXML private TableColumn<Bill, Integer> colBillId, colOrderId;
    @FXML private TableColumn<Bill, Double> colAmount;
    @FXML private TableColumn<Bill, LocalDate> colBillDate;
    @FXML private TableColumn<Bill, String> colDescription;
    @FXML private TextField txtBillId, txtAmount, txtDescription;
    @FXML private ComboBox<Integer> cmbOrderId;
    @FXML private DatePicker dpBillDate;
    @FXML private Button btnBack;

    private final BillBOImpl billService = new BillBOImpl();
    private final ObservableList<Bill> billList = FXCollections.observableArrayList();

    public void initialize() {
        colBillId.setCellValueFactory(new PropertyValueFactory<>("billId"));
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colBillDate.setCellValueFactory(new PropertyValueFactory<>("billDate"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tblBill.setItems(billList);
        loadOrderIds();
        loadAllBills();

        tblBill.getSelectionModel().selectedItemProperty().addListener((obs, old, b) -> {
            if (b != null) {
                txtBillId.setText(String.valueOf(b.getBillId()));
                cmbOrderId.setValue(b.getOrderId());
                txtAmount.setText(String.valueOf(b.getAmount()));
                dpBillDate.setValue(b.getBillDate());
                txtDescription.setText(b.getDescription());
            }
        });
    }

    @FXML void addBill(ActionEvent event) {
        if (cmbOrderId.getValue() == null || txtAmount.getText().trim().isEmpty() || dpBillDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields!"); return;
        }
        try {
            Bill bill = new Bill(0, cmbOrderId.getValue(),
                    Double.parseDouble(txtAmount.getText().trim()), dpBillDate.getValue(),
                    txtDescription.getText());
            Integer customId = txtBillId.getText().trim().isEmpty() ? null : Integer.parseInt(txtBillId.getText().trim());
            billService.addBill(bill, customId);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Bill added successfully!");
            loadAllBills(); clearFields();
        } catch (IllegalStateException e) {
            showAlert(Alert.AlertType.ERROR, "Duplicate ID", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add bill: " + e.getMessage());
        }
    }

    @FXML void updateBill(ActionEvent event) {
        if (txtBillId.getText().trim().isEmpty()) { showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a bill!"); return; }
        try {
            Bill bill = new Bill(Integer.parseInt(txtBillId.getText().trim()), cmbOrderId.getValue(),
                    Double.parseDouble(txtAmount.getText().trim()), dpBillDate.getValue(), txtDescription.getText());
            billService.updateBill(bill);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Bill updated successfully!");
            loadAllBills(); clearFields();
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update bill: " + e.getMessage()); }
    }

    @FXML void deleteBill(ActionEvent event) {
        if (txtBillId.getText().trim().isEmpty()) { showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a bill!"); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this bill?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        try {
            billService.deleteBill(Integer.parseInt(txtBillId.getText().trim()));
            showAlert(Alert.AlertType.INFORMATION, "Success", "Bill deleted successfully!");
            loadAllBills(); clearFields();
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete bill: " + e.getMessage()); }
    }

    @FXML void searchBill(ActionEvent event) {
        if (txtBillId.getText().trim().isEmpty()) { showAlert(Alert.AlertType.WARNING, "Input Required", "Enter a Bill ID!"); return; }
        try {
            java.util.List<Bill> result = billService.findById(Integer.parseInt(txtBillId.getText().trim()));
            if (result.isEmpty()) { showAlert(Alert.AlertType.INFORMATION, "Not Found", "No bill found!"); loadAllBills(); }
            else billList.setAll(result);
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Database Error", "Search failed: " + e.getMessage()); }
    }

    @FXML void btnInvoice(ActionEvent event) {
        if (txtBillId.getText().trim().isEmpty()) { showAlert(Alert.AlertType.WARNING, "Input Required", "Enter a Bill ID!"); return; }
        try {
            int billId = Integer.parseInt(txtBillId.getText().trim());
            Connection conn = DBConnection.getInstance().getConnection();
            InputStream stream = getClass().getResourceAsStream("/reports/Invoice.jrxml");
            if (stream == null) { showAlert(Alert.AlertType.ERROR, "Not Found", "Invoice.jrxml missing!"); return; }
            JasperReport report = JasperCompileManager.compileReport(stream);
            Map<String, Object> params = new HashMap<>();
            params.put("BILLID", billId);
            JasperViewer.viewReport(JasperFillManager.fillReport(report, params, conn), false);
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Report Error", e.getMessage()); }
    }

    @FXML void btnBack(ActionEvent event) { navigateToDashboard(); }

    private void loadOrderIds() {
        try { cmbOrderId.setItems(FXCollections.observableArrayList(billService.getAllOrderIds())); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void loadAllBills() {
        try { billList.setAll(billService.getAllBills()); } catch (Exception e) { e.printStackTrace(); }
    }

    private void clearFields() {
        txtBillId.clear(); cmbOrderId.setValue(null); txtAmount.clear(); txtDescription.clear(); dpBillDate.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
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
        } catch (IOException e) { showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load dashboard!"); }
    }
}
