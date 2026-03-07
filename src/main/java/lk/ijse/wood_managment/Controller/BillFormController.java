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
import lk.ijse.wood_managment.Model.Bill;
import lk.ijse.wood_managment.db.DBConnection;
import lk.ijse.wood_managment.util.UserSession;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
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

    public void initialize() {
        colBillId.setCellValueFactory(new PropertyValueFactory<>("billId"));
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colBillDate.setCellValueFactory(new PropertyValueFactory<>("billDate"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadOrders();
        loadAllBills();
        setTableSelectionListener();
    }

    private void loadOrders() {
        ObservableList<Integer> orderIds = FXCollections.observableArrayList();
        String sql = "SELECT order_id FROM customer_order ORDER BY order_id DESC";

        try (Connection con = DBConnection.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orderIds.add(rs.getInt("order_id"));
            }
            cmbOrderId.setItems(orderIds);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to load orders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAllBills() {
        ObservableList<Bill> bills = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Bill ORDER BY bill_id DESC";

        try (Connection con = DBConnection.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bills.add(new Bill(
                        rs.getInt("bill_id"),
                        rs.getInt("order_id"),
                        rs.getDouble("amount"),
                        rs.getDate("bill_date").toLocalDate(),
                        rs.getString("description")
                ));
            }
            tblBill.setItems(bills);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to load bills: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setTableSelectionListener() {
        tblBill.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtBillId.setText(String.valueOf(newSelection.getBillId()));
                cmbOrderId.setValue(newSelection.getOrderId());
                txtAmount.setText(String.valueOf(newSelection.getAmount()));
                dpBillDate.setValue(newSelection.getBillDate());
                txtDescription.setText(newSelection.getDescription());
            }
        });
    }

    @FXML
    void addBill(ActionEvent event) {
        if (cmbOrderId.getValue() == null || txtAmount.getText().trim().isEmpty() || dpBillDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields!");
            return;
        }

        try {
            Connection con = DBConnection.getInstance().getConnection();
            String sql;
            PreparedStatement pst;

            if (txtBillId.getText().trim().isEmpty()) {
                // Auto-generate bill ID
                sql = "INSERT INTO Bill(order_id, amount, bill_date, description) VALUES (?, ?, ?, ?)";
                pst = con.prepareStatement(sql);
                pst.setInt(1, cmbOrderId.getValue());
                pst.setDouble(2, Double.parseDouble(txtAmount.getText().trim()));
                pst.setDate(3, Date.valueOf(dpBillDate.getValue()));
                pst.setString(4, txtDescription.getText() == null ? "" : txtDescription.getText().trim());
            } else {
                // Check if custom Bill ID already exists
                PreparedStatement checkStmt = con.prepareStatement("SELECT COUNT(*) FROM Bill WHERE bill_id = ?");
                checkStmt.setInt(1, Integer.parseInt(txtBillId.getText().trim()));
                ResultSet rs = checkStmt.executeQuery();
                rs.next();

                if (rs.getInt(1) > 0) {
                    showAlert(Alert.AlertType.ERROR, "Duplicate ID",
                            "Bill ID already exists! Please leave it empty for auto-generation.");
                    return;
                }

                sql = "INSERT INTO Bill(bill_id, order_id, amount, bill_date, description) VALUES (?, ?, ?, ?, ?)";
                pst = con.prepareStatement(sql);
                pst.setInt(1, Integer.parseInt(txtBillId.getText().trim()));
                pst.setInt(2, cmbOrderId.getValue());
                pst.setDouble(3, Double.parseDouble(txtAmount.getText().trim()));
                pst.setDate(4, Date.valueOf(dpBillDate.getValue()));
                pst.setString(5, txtDescription.getText() == null ? "" : txtDescription.getText().trim());
            }

            pst.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Bill added successfully!");
            loadAllBills();
            clearFields();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add bill: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void updateBill(ActionEvent event) {
        if (txtBillId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a bill to update!");
            return;
        }

        if (cmbOrderId.getValue() == null || txtAmount.getText().trim().isEmpty() || dpBillDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields!");
            return;
        }

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(
                     "UPDATE Bill SET order_id=?, amount=?, bill_date=?, description=? WHERE bill_id=?")) {

            pst.setInt(1, cmbOrderId.getValue());
            pst.setDouble(2, Double.parseDouble(txtAmount.getText().trim()));
            pst.setDate(3, Date.valueOf(dpBillDate.getValue()));
            pst.setString(4, txtDescription.getText() == null ? "" : txtDescription.getText().trim());
            pst.setInt(5, Integer.parseInt(txtBillId.getText().trim()));

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Bill updated successfully!");
                loadAllBills();
                clearFields();
            } else {
                showAlert(Alert.AlertType.WARNING, "Not Found", "No bill found with that ID!");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update bill: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void deleteBill(ActionEvent event) {
        if (txtBillId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a bill to delete!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this bill?",
                ButtonType.YES, ButtonType.NO);
        confirmAlert.setTitle("Confirm Deletion");

        if (confirmAlert.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement("DELETE FROM Bill WHERE bill_id=?")) {

            pst.setInt(1, Integer.parseInt(txtBillId.getText().trim()));
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Bill deleted successfully!");
                loadAllBills();
                clearFields();
            } else {
                showAlert(Alert.AlertType.WARNING, "Not Found", "No bill found with that ID!");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid Bill ID!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete bill: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void searchBill(ActionEvent event) {
        if (txtBillId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a Bill ID to search!");
            return;
        }

        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM Bill WHERE bill_id=?")) {

            pst.setInt(1, Integer.parseInt(txtBillId.getText().trim()));
            ResultSet rs = pst.executeQuery();

            ObservableList<Bill> bills = FXCollections.observableArrayList();
            while (rs.next()) {
                bills.add(new Bill(
                        rs.getInt("bill_id"),
                        rs.getInt("order_id"),
                        rs.getDouble("amount"),
                        rs.getDate("bill_date").toLocalDate(),
                        rs.getString("description")
                ));
            }

            if (bills.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Not Found", "No bill found with ID: " + txtBillId.getText());
                loadAllBills();
            } else {
                tblBill.setItems(bills);
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Bill ID must be a valid number!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Search failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnInvoice(ActionEvent event) {
        if (txtBillId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required",
                    "Please enter a Bill ID to generate invoice!");
            return;
        }

        try {
            int billId = Integer.parseInt(txtBillId.getText().trim());
            Connection con = DBConnection.getInstance().getConnection();
            InputStream reportStream = getClass().getResourceAsStream("/reports/Invoice.jrxml");

            if (reportStream == null) {
                showAlert(Alert.AlertType.ERROR, "Report Not Found",
                        "Could not find Invoice.jrxml in resources/reports folder");
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("BILLID", billId);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, con);
            JasperViewer.viewReport(jasperPrint, false);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Bill ID must be a valid number!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Report Generation Failed", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnBack(ActionEvent event) {
        try {
            String userRole = UserSession.getUserRole();
            String fxmlPath = userRole.equals("ADMIN")
                    ? "/lk/ijse/wood_managment/admindashboard.fxml"
                    : "/lk/ijse/wood_managment/empdashboard.fxml";

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle(userRole.equals("ADMIN") ? "Admin Dashboard" : "Employee Dashboard");
            stage.centerOnScreen();
            stage.setResizable(false);

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtBillId.clear();
        cmbOrderId.setValue(null);
        txtAmount.clear();
        txtDescription.clear();
        dpBillDate.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}