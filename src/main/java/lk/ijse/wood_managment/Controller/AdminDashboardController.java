package lk.ijse.wood_managment.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private Button btnBack;

    @FXML
    private void openCustomerForm(ActionEvent event) {
        openNewWindow("/lk/ijse/wood_managment/customerform.fxml", "Customer Details", event);
    }

    @FXML
    private void openEmployeeForm(ActionEvent event) {
        openNewWindow("/lk/ijse/wood_managment/employeeform.fxml", "Employee Details", event);
    }

    @FXML
    private void openOrderDetails(ActionEvent event) {
        openNewWindow("/lk/ijse/wood_managment/orderform.fxml", "Order Details", event);
    }

    @FXML
    private void openTimberDetails(ActionEvent event) {
        openNewWindow("/lk/ijse/wood_managment/timberform.fxml", "Timber Details", event);
    }

    @FXML
    private void openCuttingDetails(ActionEvent event) {
        openNewWindow("/lk/ijse/wood_managment/cuttingform.fxml", "Cutting Details", event);
    }

    @FXML
    private void openSupplierDetails(ActionEvent event) {
        openNewWindow("/lk/ijse/wood_managment/supplierform.fxml", "Supplier Details", event);
    }

    @FXML
    private void openRegisterDetails(ActionEvent event) {
        openNewWindow("/lk/ijse/wood_managment/registerform.fxml", "Register Details", event);
    }

    @FXML
    private void openWoodDetails(ActionEvent event) {
        openNewWindow("/lk/ijse/wood_managment/woodform.fxml", "Wood Details", event);
    }

    @FXML
    private void openBillDetails(ActionEvent event) {
        openNewWindow("/lk/ijse/wood_managment/billform.fxml", "Bill Details", event);
    }

    @FXML
    private void openExpenseDetails(ActionEvent event) {
        openNewWindow("/lk/ijse/wood_managment/expenseform.fxml", "Expense Details", event);
    }

    @FXML
    private void openCalculator(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lk/ijse/wood_managment/cal.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 400, 524));
        stage.setTitle("Calculator Details");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void backToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/lk/ijse/wood_managment/loginviwe.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle("Login");
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openNewWindow(String fxmlPath, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.close();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}