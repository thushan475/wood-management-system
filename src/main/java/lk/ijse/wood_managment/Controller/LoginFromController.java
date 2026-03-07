package lk.ijse.wood_managment.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.ijse.wood_managment.util.UserSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFromController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private void loginOnAction(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wood_management",
                "root",
                "mysql")) {


            String sql = "SELECT * FROM register WHERE BINARY username = ? AND password=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                UserSession.setUserRole(role.toUpperCase());
                UserSession.setUsername(rs.getString("username"));

//                new Alert(Alert.AlertType.INFORMATION, "Login Successful!").show();

                openDashboardForRole(role, event);

            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid Username or Password!").show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database Error!").show();
        }
    }

    private void openDashboardForRole(String role, ActionEvent event) {
        try {
            String fxmlPath;
            String title;

            if ("ADMIN".equalsIgnoreCase(role)) {
                fxmlPath = "/lk/ijse/wood_managment/admindashboard.fxml";
                title = "Admin Dashboard";
            } else {
                fxmlPath = "/lk/ijse/wood_managment/empdashboard.fxml";
                title = "Employee Dashboard";
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open dashboard!").show();
        }
    }
}

