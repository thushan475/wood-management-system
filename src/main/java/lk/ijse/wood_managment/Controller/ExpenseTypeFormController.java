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
import lk.ijse.wood_managment.Dto.ExpenseTypeDTO;
import lk.ijse.wood_managment.Model.expensetype;

import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Consumer;

public class ExpenseTypeFormController {

    @FXML private TextField txtExpenseId;
    @FXML private TextField txtExpenseName;
    @FXML private TableView<ExpenseTypeDTO> tblExpense;
    @FXML private TableColumn<ExpenseTypeDTO, Integer> colId;
    @FXML private TableColumn<ExpenseTypeDTO, String> colName;

    private Consumer<String> onExpenseTypeAdded;

    public void setOnExpenseTypeAdded(Consumer<String> callback) {
        this.onExpenseTypeAdded = callback;
    }

    public void initialize() {
        colId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("expenseId"));
        colName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("expenseName"));
        loadTable();
    }

    private void loadTable() {
        try {
            ObservableList<ExpenseTypeDTO> list = FXCollections.observableArrayList(expensetype.getAll());
            tblExpense.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void add(ActionEvent event) {
        try {
            ExpenseTypeDTO dto = new ExpenseTypeDTO(
                    Integer.parseInt(txtExpenseId.getText()),
                    txtExpenseName.getText()
            );
            if(expensetype.save(dto)) {
                new Alert(Alert.AlertType.INFORMATION, "Added Successfully").show();
                loadTable();
                clearFields();
                if(onExpenseTypeAdded != null) onExpenseTypeAdded.accept(dto.getExpenseName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void update(ActionEvent event) {
        try {
            ExpenseTypeDTO dto = new ExpenseTypeDTO(
                    Integer.parseInt(txtExpenseId.getText()),
                    txtExpenseName.getText()
            );
            if(expensetype.update(dto)) {
                new Alert(Alert.AlertType.INFORMATION, "Updated Successfully").show();
                loadTable();
                clearFields();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void delete(ActionEvent event) {
        try {
            if(expensetype.delete(Integer.parseInt(txtExpenseId.getText()))) {
                new Alert(Alert.AlertType.INFORMATION, "Deleted Successfully").show();
                loadTable();
                clearFields();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void search(ActionEvent event) {
        try {
            if(!txtExpenseId.getText().isEmpty()){
                ExpenseTypeDTO dto = expensetype.search(Integer.parseInt(txtExpenseId.getText()));
                if(dto != null) txtExpenseName.setText(dto.getExpenseName());
                else new Alert(Alert.AlertType.WARNING, "Not Found").show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Enter expense id first").show();
            }

        } catch (Exception e) {

        }
    }
    @FXML
    private Button btnBack;

    @FXML
    void back(ActionEvent event) {
        try {
            Stage stage = (Stage) btnBack.getScene().getWindow();
            Stage currentStage = (Stage) btnBack.getScene().getWindow();
            currentStage.close();
            Parent root = FXMLLoader.load(getClass().getResource("/lk/ijse/wood_managment/expenseform.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Expense Form");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void clearFields() {
        txtExpenseId.clear();
        txtExpenseName.clear();
    }
    @FXML
    private void clickExpense (MouseEvent event){
        if (event.getClickCount()==1) {
            ExpenseTypeDTO expenses = tblExpense.getSelectionModel().getSelectedItem();
            txtExpenseId.setText(Integer.toString(expenses.getExpenseId()));
            txtExpenseName.setText(expenses.getExpenseName());
        }
    }
}
