package lk.ijse.wood_management.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lk.ijse.wood_management.db.DBConnection;
import lk.ijse.wood_management.dto.ExpenseDTO;
import lk.ijse.wood_management.dto.ExpenseTypeDTO;
import lk.ijse.wood_management.bo.custom.impl.ExpenseBOImpl;
import lk.ijse.wood_management.bo.custom.impl.ExpenseTypeBOImpl;
import lk.ijse.wood_management.util.UserSession;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseFormController {

    @FXML private TextField txtDescription, txtAmount;
    @FXML private ComboBox<String> cmbExpenseType;
    @FXML private DatePicker dpExpenseDate;
    @FXML private Button back;

    @FXML private TableView<ExpenseDTO> tblExpense;
    @FXML private TableColumn<ExpenseDTO, Integer> colExpenseId, colWoodId;
    @FXML private TableColumn<ExpenseDTO, String> colExpenseType, colDescription, colSpecies, colExpenseDate;
    @FXML private TableColumn<ExpenseDTO, Double> colLength, colWidth, colQtyPrice, colAmount;

    private final ExpenseBOImpl expenseService = new ExpenseBOImpl();
    private final ExpenseTypeBOImpl expenseTypeService = new ExpenseTypeBOImpl();

    @FXML public void initialize() {
        colExpenseId.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getExpenseId()));
        colWoodId.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getWoodId()));
        colExpenseType.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getExpenseType()));
        colDescription.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDescription()));
        colSpecies.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getSpecies() == null ? "" : data.getValue().getSpecies()));
        colLength.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getLength()));
        colWidth.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getWidth()));
        colQtyPrice.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getQtyPrice()));
        colAmount.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAmount()));
        colExpenseDate.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(
                data.getValue().getExpenseDate() != null ? data.getValue().getExpenseDate().toString() : ""));

        tblExpense.setOnMouseClicked(this::tableRowClicked);
        loadAllExpenses();
        loadExpenseTypes();
    }

    @FXML private void btnAdd(ActionEvent event) {
        if (!validateInputs()) return;
        try {
            ExpenseDTO dto = new ExpenseDTO(cmbExpenseType.getValue(), txtDescription.getText().trim(),
                    Double.parseDouble(txtAmount.getText().trim()), dpExpenseDate.getValue());
            if (expenseService.addExpense(dto)) {
                loadAllExpenses(); clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Expense added successfully!");
            } else showAlert(Alert.AlertType.ERROR, "Error", "Failed to add expense");
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed to add expense: " + e.getMessage()); }
    }

    @FXML private void btnupdate(ActionEvent event) {
        ExpenseDTO selected = tblExpense.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Select", "Please select a row first"); return; }
        if (!validateInputs()) return;
        try {
            selected.setExpenseType(cmbExpenseType.getValue());
            selected.setDescription(txtDescription.getText().trim());
            selected.setAmount(Double.parseDouble(txtAmount.getText().trim()));
            selected.setExpenseDate(dpExpenseDate.getValue());
            if (expenseService.updateExpense(selected)) {
                loadAllExpenses(); clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Updated", "Expense updated successfully");
            } else showAlert(Alert.AlertType.ERROR, "Error", "Failed to update expense");
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed to update: " + e.getMessage()); }
    }

    @FXML private void btndelete(ActionEvent event) {
        ExpenseDTO selected = tblExpense.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Select", "Please select a row to delete"); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this expense?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                if (expenseService.deleteExpense(selected.getExpenseId())) {
                    loadAllExpenses(); clearFields();
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Expense deleted successfully");
                } else showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete expense");
            } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete: " + e.getMessage()); }
        }
    }

    @FXML private void btnsearch(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Expense"); dialog.setHeaderText("Enter Expense ID"); dialog.setContentText("Expense ID:");
        dialog.showAndWait().ifPresent(id -> {
            try {
                ExpenseDTO e = expenseService.findById(Integer.parseInt(id.trim()));
                if (e != null) { fillFields(e); showAlert(Alert.AlertType.INFORMATION, "Found", "Expense found"); }
                else showAlert(Alert.AlertType.WARNING, "Not Found", "No expense found with ID: " + id);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID format.");
            } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Error", "Search failed: " + ex.getMessage()); }
        });
    }

    @FXML private void btnBack(ActionEvent event) { navigateToDashboard(); }

    @FXML private void btnExpense(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lk/ijse/wood_management/expensetype.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Expense Type"); stage.setScene(new Scene(root, 1104, 622));
            stage.setResizable(false); stage.show();
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Cannot load Expense Type form!"); }
    }

    @FXML private void btnreport(ActionEvent event) {
        ExpenseDTO selected = tblExpense.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Select Row", "Please select an expense row first"); return; }
        try {
            LocalDate date = selected.getExpenseDate();
            Map<String, Object> params = new HashMap<>();
            params.put("year", date.getYear()); params.put("month", date.getMonthValue());
            Connection conn = DBConnection.getInstance().getConnection();
            InputStream is = getClass().getResourceAsStream("/reports/report.jrxml");
            if (is == null) { showAlert(Alert.AlertType.ERROR, "Error", "report.jrxml not found!"); return; }
            JasperReport report = JasperCompileManager.compileReport(is);
            JasperViewer.viewReport(JasperFillManager.fillReport(report, params, conn), false);
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Report Error", "Failed to generate report"); }
    }

    private void tableRowClicked(MouseEvent event) {
        ExpenseDTO selected = tblExpense.getSelectionModel().getSelectedItem();
        if (selected != null) fillFields(selected);
    }

    private void fillFields(ExpenseDTO e) {
        cmbExpenseType.setValue(e.getExpenseType());
        txtDescription.setText(e.getDescription() != null ? e.getDescription() : "");
        txtAmount.setText(e.getAmount() != null ? e.getAmount().toString() : "");
        dpExpenseDate.setValue(e.getExpenseDate());
    }

    private void clearFields() {
        cmbExpenseType.setValue(null); txtDescription.clear();
        txtAmount.clear(); dpExpenseDate.setValue(null);
        tblExpense.getSelectionModel().clearSelection();
    }

    private boolean validateInputs() {
        if (cmbExpenseType.getValue() == null || cmbExpenseType.getValue().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select an expense type"); return false;
        }
        if (txtDescription.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a description"); return false;
        }
        if (txtAmount.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter an amount"); return false;
        }
        try {
            if (Double.parseDouble(txtAmount.getText().trim()) <= 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Amount must be greater than 0"); return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid amount format"); return false;
        }
        if (dpExpenseDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select an expense date"); return false;
        }
        return true;
    }

    private void loadAllExpenses() {
        try { tblExpense.setItems(FXCollections.observableArrayList(expenseService.getAllExpenses())); }
        catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Error", "Failed to load expenses: " + e.getMessage()); }
    }

    private void loadExpenseTypes() {
        try {
            List<ExpenseTypeDTO> types = expenseTypeService.getAll();
            cmbExpenseType.setItems(FXCollections.observableArrayList(
                    types.stream().map(ExpenseTypeDTO::getExpenseName).toList()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type); a.setHeaderText(null); a.setTitle(title); a.setContentText(msg); a.showAndWait();
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
