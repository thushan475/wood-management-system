package lk.ijse.wood_managment.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lk.ijse.wood_managment.util.UserSession;

import java.io.IOException;

public class EmpDashboardController {

    @FXML
    private Button btnBackLogin;
    @FXML
    private Label lblTitle;

    @FXML
    public void initialize() {
        lblTitle.setText("Welcome Employee!");
    }

    @FXML
    private void customerDetailsOnAction(ActionEvent event) {
        openWindow("/lk/ijse/wood_managment/customerform.fxml", "Customer Details", event);
    }

    @FXML
    private void orderDetailsOnAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/lk/ijse/wood_managment/orderform.fxml")
            );
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                    .getScene().getWindow();

            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle("Order Details");
            stage.centerOnScreen();
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void woodDetailsOnAction(ActionEvent event) {
        openWindow("/lk/ijse/wood_managment/woodform.fxml", "Wood Details", event);
    }

    @FXML
    private void timberDetailsOnAction(ActionEvent event) {
        openWindow("/lk/ijse/wood_managment/timberform.fxml", "Timber Details", event);
    }

    @FXML
    private void cuttingDetailsOnAction(ActionEvent event) {
        openWindow("/lk/ijse/wood_managment/cuttingform.fxml", "Cutting Details", event);
    }

    @FXML
    private void billDetailsOnAction(ActionEvent event) {
        openWindow("/lk/ijse/wood_managment/billform.fxml", "Bill Details", event);
    }

    @FXML
    private void employeeDetailsOnAction(ActionEvent event) {
        openWindow("/lk/ijse/wood_managment/employeeform.fxml", "Employee Details", event);
    }
    @FXML
    private void openCalculator(ActionEvent event) throws IOException{
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

    private void openWindow(String fxmlPath, String title, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                    .getScene().getWindow();

            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
