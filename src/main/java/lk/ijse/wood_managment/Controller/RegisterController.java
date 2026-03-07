package lk.ijse.wood_managment.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RegisterController {
    @FXML
    private  Button btnBack;
    @FXML
    private TextField txtUserName, txtEmail, txtContact;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private ComboBox<String> cmbRole;

    @FXML
    private DatePicker dpCreateDate;

    @FXML
    public void initialize() {
        cmbRole.getItems().addAll("Admin", "Employee");


        txtUserName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                loadUserDetails(newValue);
            } else {
                clearFields();
            }
        });
    }
    @FXML
    private void SubmitOnAction(ActionEvent event) {
        String username = txtUserName.getText().trim();
        String password = txtPassword.getText().trim();
        String email = txtEmail.getText().trim();
        String contact = txtContact.getText().trim();
        String role = cmbRole.getValue();
        LocalDate createDate = dpCreateDate.getValue();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()
                || contact.isEmpty() || role == null || createDate == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields!").show();
            return;
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid Gmail address!").show();
            txtEmail.requestFocus();
            return;
        }

        if (!contact.matches("\\d{10}")) {
            new Alert(Alert.AlertType.ERROR, "Contact number must contain exactly 10 digits!").show();
            txtContact.requestFocus();
            return;
        }

        String jdbcURL = "jdbc:mysql://localhost:3306/wood_management?useSSL=false&allowPublicKeyRetrieval=true";
        String dbUser = "root";
        String dbPassword = "mysql";

        String sql = "INSERT INTO register(username, password, email, contact, role, create_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, email);
            pst.setString(4, contact);
            pst.setString(5, role);
            pst.setDate(6, java.sql.Date.valueOf(createDate));

            int res = pst.executeUpdate();
            if (res > 0) {
                new Alert(Alert.AlertType.INFORMATION, "User Registered Successfully!").show();
                clearFields();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database Error! " + e.getMessage()).show();
        }
    }


    @FXML
    private void btnDelete(ActionEvent event) {
        String username = txtUserName.getText().trim();
        if (username.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter the User Name to delete!");
            alert.show();
            return;
        }

        String jdbcURL = "jdbc:mysql://localhost:3306/wood_management?useSSL=false&allowPublicKeyRetrieval=true";
        String dbUser = "root";
        String dbPassword = "mysql";

        String deleteSQL = "DELETE FROM register WHERE username = ?";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this user?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() != ButtonType.YES) {
            return;
        }

        try (Connection con = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
             PreparedStatement deletePst = con.prepareStatement(deleteSQL)) {

            deletePst.setString(1, username);
            int res = deletePst.executeUpdate();
            if (res > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "User deleted successfully!");
                alert.show();
                clearFields();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "No user found with this User Name!");
                alert.show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void BackToLoginOnAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/lk/ijse/wood_managment/admindashboard.fxml"));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle("Admin Dashboard");
            stage.centerOnScreen();
            stage.setResizable(false);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Cannot load Admin Dashboard!").show();
            e.printStackTrace();
        }
    }



    private void loadUserDetails(String username) {
        String jdbcURL = "jdbc:mysql://localhost:3306/wood_management?useSSL=false&allowPublicKeyRetrieval=true";
        String dbUser = "root";
        String dbPassword = "mysql";

        String selectSQL = "SELECT * FROM register WHERE username = ?";

        try (Connection con = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
             PreparedStatement pst = con.prepareStatement(selectSQL)) {

            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                txtPassword.setText(rs.getString("password"));
                txtEmail.setText(rs.getString("email"));
                txtContact.setText(rs.getString("contact"));
                cmbRole.setValue(rs.getString("role"));
                dpCreateDate.setValue(rs.getDate("create_date").toLocalDate());
            } else {
                txtPassword.clear();
                txtEmail.clear();
                txtContact.clear();
                cmbRole.getSelectionModel().clearSelection();
                dpCreateDate.setValue(null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtUserName.clear();
        txtPassword.clear();
        txtEmail.clear();
        txtContact.clear();
        cmbRole.getSelectionModel().clearSelection();
        dpCreateDate.setValue(null);
    }
}
