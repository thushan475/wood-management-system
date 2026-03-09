package lk.ijse.wood_management.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lk.ijse.wood_management.dto.ExpenseTypeDTO;
import lk.ijse.wood_management.bo.custom.impl.ExpenseTypeBOImpl;

import java.io.IOException;
import java.util.function.Consumer;

public class ExpenseTypeFormController {

    @FXML private TextField txtExpenseId, txtExpenseName;
    @FXML private TableView<ExpenseTypeDTO> tblExpense;
    @FXML private TableColumn<ExpenseTypeDTO, Integer> colId;
    @FXML private TableColumn<ExpenseTypeDTO, String> colName;
    @FXML private Button btnBack;

    private final ExpenseTypeBOImpl expenseTypeService = new ExpenseTypeBOImpl();
    private Consumer<String> onExpenseTypeAdded;

    public void setOnExpenseTypeAdded(Consumer<String> callback) { this.onExpenseTypeAdded = callback; }

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("expenseId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("expenseName"));
        loadTable();
    }

    @FXML void add(ActionEvent event) {
        try {
            ExpenseTypeDTO dto = new ExpenseTypeDTO(Integer.parseInt(txtExpenseId.getText()), txtExpenseName.getText());
            if (expenseTypeService.add(dto)) {
                new Alert(Alert.AlertType.INFORMATION, "Added Successfully").show();
                loadTable(); clearFields();
                if (onExpenseTypeAdded != null) onExpenseTypeAdded.accept(dto.getExpenseName());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void update(ActionEvent event) {
        try {
            ExpenseTypeDTO dto = new ExpenseTypeDTO(Integer.parseInt(txtExpenseId.getText()), txtExpenseName.getText());
            if (expenseTypeService.update(dto)) {
                new Alert(Alert.AlertType.INFORMATION, "Updated Successfully").show();
                loadTable(); clearFields();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void delete(ActionEvent event) {
        try {
            if (expenseTypeService.delete(Integer.parseInt(txtExpenseId.getText()))) {
                new Alert(Alert.AlertType.INFORMATION, "Deleted Successfully").show();
                loadTable(); clearFields();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void search(ActionEvent event) {
        if (txtExpenseId.getText().isEmpty()) { new Alert(Alert.AlertType.ERROR, "Enter expense id first").show(); return; }
        try {
            ExpenseTypeDTO dto = expenseTypeService.findById(Integer.parseInt(txtExpenseId.getText()));
            if (dto != null) txtExpenseName.setText(dto.getExpenseName());
            else new Alert(Alert.AlertType.WARNING, "Not Found").show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML void back(ActionEvent event) {
        try {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.close();
            Parent root = FXMLLoader.load(getClass().getResource("/lk/ijse/wood_management/expenseform.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Expense Form");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void clickExpense(MouseEvent event) {
        if (event.getClickCount() == 1) {
            ExpenseTypeDTO selected = tblExpense.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtExpenseId.setText(Integer.toString(selected.getExpenseId()));
                txtExpenseName.setText(selected.getExpenseName());
            }
        }
    }

    private void loadTable() {
        try { tblExpense.setItems(FXCollections.observableArrayList(expenseTypeService.getAll())); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void clearFields() { txtExpenseId.clear(); txtExpenseName.clear(); }
}
