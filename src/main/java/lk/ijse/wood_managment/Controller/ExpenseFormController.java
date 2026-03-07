package lk.ijse.wood_managment.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lk.ijse.wood_managment.Dto.ExpenseDTO;
import lk.ijse.wood_managment.Dto.ExpenseTypeDTO;
import lk.ijse.wood_managment.Model.Expense;
import lk.ijse.wood_managment.Model.expensetype;
import lk.ijse.wood_managment.db.DBConnection;
import lk.ijse.wood_managment.util.UserSession;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseFormController {

    @FXML private TextField txtDescription;
    @FXML private TextField txtAmount;
    @FXML private ComboBox<String> cmbExpenseType;
    @FXML private DatePicker dpExpenseDate;
    @FXML private Button back;

    @FXML private TableView<ExpenseDTO> tblExpense;
    @FXML private TableColumn<ExpenseDTO, Integer> colExpenseId;
    @FXML private TableColumn<ExpenseDTO, Integer> colWoodId;
    @FXML private TableColumn<ExpenseDTO, String> colExpenseType;
    @FXML private TableColumn<ExpenseDTO, String> colDescription;
    @FXML private TableColumn<ExpenseDTO, String> colSpecies;
    @FXML private TableColumn<ExpenseDTO, Double> colLength;
    @FXML private TableColumn<ExpenseDTO, Double> colWidth;
    @FXML private TableColumn<ExpenseDTO, Double> colQtyPrice;
    @FXML private TableColumn<ExpenseDTO, Double> colAmount;
    @FXML private TableColumn<ExpenseDTO, String> colExpenseDate;

    private Expense expenseModel = new Expense();
    private expensetype exp=new expensetype();


    @FXML
    public void initialize() {
        cmbExpenseType.setItems(FXCollections.observableArrayList(
                "Employee Salary", "Buy Wood", "Water Bill", "Electric Bill", "Machine Expense"
        ));

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
                data.getValue().getExpenseDate() != null ? data.getValue().getExpenseDate().toString() : ""
        ));

        tblExpense.setOnMouseClicked(this::tableRowClicked);
        loadAllExpenses();
        loadexpense();
    }

    @FXML
    private void btnAdd(ActionEvent event) {
        if (!validateInputs()) return;

        String type = cmbExpenseType.getSelectionModel().getSelectedItem();
        String des = txtDescription.getText().trim();
        double amount = Double.parseDouble(txtAmount.getText().trim());
        LocalDate date = dpExpenseDate.getValue();

        try {
            ExpenseDTO expenseDTO = new ExpenseDTO(type, des, amount, date);
            if (expenseModel.addExpense(expenseDTO)) {
                loadAllExpenses();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Expense added successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add expense");
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add expense: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void btnupdate(ActionEvent event) {
        ExpenseDTO selected = tblExpense.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Select", "Please select a row first");
            return;
        }
        if (!validateInputs()) return;

        try {
            selected.setExpenseType(cmbExpenseType.getValue());
            selected.setDescription(txtDescription.getText().trim());
            selected.setAmount(Double.parseDouble(txtAmount.getText().trim()));
            selected.setExpenseDate(dpExpenseDate.getValue());

            if (expenseModel.updateExpense(selected)) {
                loadAllExpenses();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Updated", "Expense updated successfully");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update expense");
            }
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void btndelete(ActionEvent event) {
        ExpenseDTO selected = tblExpense.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Select", "Please select a row to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this expense?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                if (expenseModel.deleteExpense(selected.getExpenseId())) {
                    loadAllExpenses();
                    clearFields();
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Expense deleted successfully");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete expense");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void btnsearch(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Expense");
        dialog.setHeaderText("Enter Expense ID");
        dialog.setContentText("Expense ID:");

        dialog.showAndWait().ifPresent(id -> {
            try {
                int expenseId = Integer.parseInt(id.trim());
                ExpenseDTO e = expenseModel.searchExpense(expenseId);
                if (e != null) {
                    fillFields(e);
                    showAlert(Alert.AlertType.INFORMATION, "Found", "Expense found");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "No expense found with ID: " + expenseId);
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID format. Please enter a number.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Search failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void btnBack(ActionEvent event) {
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




    @FXML
    private void btnExpense(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lk/ijse/wood_managment/expensetype.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Expense Type");
            stage.setScene(new Scene(root, 1104, 622));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load Expense Type form!");
            e.printStackTrace();
        }
    }
    @FXML
    private void btnreport(ActionEvent event) {

        ExpenseDTO selected = tblExpense.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Select Row", "Please select an expense row first");
            return;
        }

        try {

            LocalDate date = selected.getExpenseDate();
            int year = date.getYear();
            int month = date.getMonthValue();


            Map<String, Object> params = new HashMap<>();
            params.put("year", year);
            params.put("month", month);


            Connection con = DBConnection.getInstance().getConnection();


            InputStream is = getClass().getResourceAsStream(
                    "/reports/report.jrxml"
            );

            if (is == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invoice.jrxml not found!");
                return;
            }


            JasperReport jasperReport = JasperCompileManager.compileReport(is);


            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    params,
                    con
            );


            JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Report Error", "Failed to generate report");
            e.printStackTrace();
        }
    }



    private void tableRowClicked(MouseEvent event) {
        ExpenseDTO selected = tblExpense.getSelectionModel().getSelectedItem();
        if (selected != null) {
            fillFields(selected);
        }
    }

    private void fillFields(ExpenseDTO e) {
        cmbExpenseType.setValue(e.getExpenseType());
        txtDescription.setText(e.getDescription() != null ? e.getDescription() : "");
        txtAmount.setText(e.getAmount() != null ? e.getAmount().toString() : "");
        dpExpenseDate.setValue(e.getExpenseDate());
    }

    private void clearFields() {
        cmbExpenseType.setValue(null);
        txtDescription.clear();
        txtAmount.clear();
        dpExpenseDate.setValue(null);
        tblExpense.getSelectionModel().clearSelection();
    }

    private boolean validateInputs() {
        if (cmbExpenseType.getValue() == null || cmbExpenseType.getValue().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select an expense type");
            return false;
        }

        if (txtDescription.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a description");
            return false;
        }

        if (txtAmount.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter an amount");
            return false;
        }

        try {
            double amount = Double.parseDouble(txtAmount.getText().trim());
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Amount must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid amount format");
            return false;
        }

        if (dpExpenseDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select an expense date");
            return false;
        }

        return true;
    }

    private void loadAllExpenses() {
        try {
            List<ExpenseDTO> list = expenseModel.getAllExpenses();
            tblExpense.setItems(FXCollections.observableArrayList(list != null ? list : List.of()));
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load expenses: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadexpense (){
        try{
            List<ExpenseTypeDTO> expense= expensetype.getAll();
            ObservableList<String> expenseList=FXCollections.observableArrayList();
            for(ExpenseTypeDTO expenseTypeDTO:expense){
                expenseList.add(expenseTypeDTO.getExpenseName());
            }
            cmbExpenseType.setItems(expenseList);

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}