package lk.ijse.wood_managment.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.wood_managment.Model.Employee;
import lk.ijse.wood_managment.util.UserSession;

import java.sql.*;

public class EmployeeFormController {
    @FXML private Button btnBack;
    @FXML private TextField txtEmployeeId, txtUserId, txtName, txtRole, txtContact, txtSalary;
    @FXML private Button btnAdd, btnUpdate, btnDelete, btnSearch;
    @FXML private TableView<Employee> tblEmployee;
    @FXML private TableColumn<Employee, Integer> colEmpId, colUserId;
    @FXML private TableColumn<Employee, String> colName, colRole, colContact;
    @FXML private TableColumn<Employee, Double> colSalary;

    private final String jdbcURL = "jdbc:mysql://localhost:3306/wood_management?useSSL=false&allowPublicKeyRetrieval=true";
    private final String dbUser = "root";
    private final String dbPassword = "mysql";

    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();
    private String dashboardType;

    @FXML
    private Button back;
    @FXML
    public void initialize() {

        colEmpId.setCellValueFactory(cell -> cell.getValue().employeeIdProperty().asObject());
        colUserId.setCellValueFactory(cell -> cell.getValue().userIdProperty().asObject());
        colName.setCellValueFactory(cell -> cell.getValue().nameProperty());
        colRole.setCellValueFactory(cell -> cell.getValue().roleProperty());
        colContact.setCellValueFactory(cell -> cell.getValue().contactProperty());
        colSalary.setCellValueFactory(cell -> cell.getValue().salaryProperty().asObject());


        colSalary.setCellFactory(tc -> new TableCell<Employee, Double>() {
            @Override
            protected void updateItem(Double salary, boolean empty) {
                super.updateItem(salary, empty);
                if (empty || salary == null) {
                    setText(null);
                } else {
                    setText(String.format("%.0f", salary));
                }
            }
        });

        tblEmployee.setItems(employeeList);
        loadEmployees();


        tblEmployee.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtEmployeeId.setText(String.valueOf(newSelection.getEmployeeId()));
                txtUserId.setText(String.valueOf(newSelection.getUserId()));
                txtName.setText(newSelection.getName());
                txtRole.setText(newSelection.getRole());
                txtContact.setText(newSelection.getContact());
                txtSalary.setText(String.valueOf(newSelection.getSalary()));
            }
        });
    }


    private void loadEmployees() {
        employeeList.clear();
        String sql = "SELECT * FROM employee_details";
        try (Connection con = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employeeList.add(new Employee(
                        rs.getInt("employee_id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("contact_number"),
                        rs.getDouble("salary")
                ));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void addEmployee() {
        if (!validateFields()) return;

        String sql = "INSERT INTO employee_details(employee_id, user_id, name, role, contact_number, salary) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, Integer.parseInt(txtEmployeeId.getText()));
            pst.setInt(2, Integer.parseInt(txtUserId.getText()));
            pst.setString(3, txtName.getText());
            pst.setString(4, txtRole.getText());
            pst.setString(5, txtContact.getText());
            pst.setDouble(6, Double.parseDouble(txtSalary.getText()));

            int rows = pst.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee added successfully!");
                clearFields();
                loadEmployees();
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void updateEmployee() {
        if (!validateFields()) return;

        String sql = "UPDATE employee_details SET user_id=?, name=?, role=?, contact_number=?, salary=? WHERE employee_id=?";
        try (Connection con = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, Integer.parseInt(txtUserId.getText()));
            pst.setString(2, txtName.getText());
            pst.setString(3, txtRole.getText());
            pst.setString(4, txtContact.getText());
            pst.setDouble(5, Double.parseDouble(txtSalary.getText()));
            pst.setInt(6, Integer.parseInt(txtEmployeeId.getText()));

            int rows = pst.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee updated successfully!");
                clearFields();
                loadEmployees();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Employee not found!");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void deleteEmployee() {
        String empId = txtEmployeeId.getText();
        if (empId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter Employee ID");
            return;
        }

        String sql = "DELETE FROM employee_details WHERE employee_id=?";
        try (Connection con = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, Integer.parseInt(empId));
            int rows = pst.executeUpdate();

            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee deleted successfully!");
                clearFields();
                loadEmployees();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Employee not found!");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void searchEmployee() {
        String empId = txtEmployeeId.getText();
        String empName = txtName.getText();

        if (empId.isEmpty() && empName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter Employee ID or Name to search");
            return;
        }

        String sql;
        if (!empId.isEmpty()) {
            sql = "SELECT * FROM employee_details WHERE employee_id=?";
        } else {
            sql = "SELECT * FROM employee_details WHERE name LIKE ?";
        }

        try (Connection con = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
             PreparedStatement pst = con.prepareStatement(sql)) {

            if (!empId.isEmpty()) {
                pst.setInt(1, Integer.parseInt(empId));
            } else {
                pst.setString(1, "%" + empName + "%");
            }

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                txtEmployeeId.setText(String.valueOf(rs.getInt("employee_id")));
                txtUserId.setText(String.valueOf(rs.getInt("user_id")));
                txtName.setText(rs.getString("name"));
                txtRole.setText(rs.getString("role"));
                txtContact.setText(rs.getString("contact_number"));
                txtSalary.setText(String.valueOf(rs.getDouble("salary")));
                showAlert(Alert.AlertType.INFORMATION, "Found", "Employee found!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Employee not found!");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private boolean validateFields() {
        if (txtEmployeeId.getText().isEmpty() ||
                txtUserId.getText().isEmpty() ||
                txtName.getText().isEmpty() ||
                txtRole.getText().isEmpty() ||
                txtContact.getText().isEmpty() ||
                txtSalary.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields");
            return false;
        }

        String contact = txtContact.getText().trim();
        if (!contact.matches("\\d{10}")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Contact number must contain exactly 10 digits");
            txtContact.requestFocus();
            return false;
        }

        try {
            Double.parseDouble(txtSalary.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Salary must be a valid number");
            txtSalary.requestFocus();
            return false;
        }

        return true;
    }


    private void clearFields() {
        txtEmployeeId.clear();
        txtUserId.clear();
        txtName.clear();
        txtRole.clear();
        txtContact.clear();
        txtSalary.clear();
    }

    @FXML
    private void btnBackOnAction(ActionEvent event) {
        try {
            String role = UserSession.getUserRole();
            String fxmlPath = role.equals("ADMIN")
                    ? "/lk/ijse/wood_managment/admindashboard.fxml"
                    : "/lk/ijse/wood_managment/empdashboard.fxml";

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle(role.equals("ADMIN") ? "Admin Dashboard" : "Employee Dashboard");
            stage.centerOnScreen();
            stage.setResizable(false);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Cannot return to dashboard!").show();
            e.printStackTrace();
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
