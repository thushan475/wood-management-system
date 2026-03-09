package lk.ijse.wood_management.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.wood_management.entity.Employee;
import lk.ijse.wood_management.bo.custom.impl.EmployeeBOImpl;
import lk.ijse.wood_management.util.UserSession;

public class EmployeeFormController {

    @FXML private Button btnBack, back;
    @FXML private TextField txtEmployeeId, txtUserId, txtName, txtRole, txtContact, txtSalary;
    @FXML private TableView<Employee> tblEmployee;
    @FXML private TableColumn<Employee, Integer> colEmpId, colUserId;
    @FXML private TableColumn<Employee, String> colName, colRole, colContact;
    @FXML private TableColumn<Employee, Double> colSalary;

    private final EmployeeBOImpl employeeService = new EmployeeBOImpl();
    private final ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    @FXML public void initialize() {
        colEmpId.setCellValueFactory(cell -> cell.getValue().employeeIdProperty().asObject());
        colUserId.setCellValueFactory(cell -> cell.getValue().userIdProperty().asObject());
        colName.setCellValueFactory(cell -> cell.getValue().nameProperty());
        colRole.setCellValueFactory(cell -> cell.getValue().roleProperty());
        colContact.setCellValueFactory(cell -> cell.getValue().contactProperty());
        colSalary.setCellValueFactory(cell -> cell.getValue().salaryProperty().asObject());

        colSalary.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double salary, boolean empty) {
                super.updateItem(salary, empty);
                setText(empty || salary == null ? null : String.format("%.0f", salary));
            }
        });

        tblEmployee.setItems(employeeList);
        loadEmployees();

        tblEmployee.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                txtEmployeeId.setText(String.valueOf(selected.getEmployeeId()));
                txtUserId.setText(String.valueOf(selected.getUserId()));
                txtName.setText(selected.getName());
                txtRole.setText(selected.getRole());
                txtContact.setText(selected.getContact());
                txtSalary.setText(String.valueOf(selected.getSalary()));
            }
        });
    }

    @FXML private void addEmployee() {
        if (!validateFields()) return;
        try {
            employeeService.addEmployee(buildEntity());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Employee added successfully!");
            clearFields(); loadEmployees();
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage()); }
    }

    @FXML private void updateEmployee() {
        if (!validateFields()) return;
        try {
            if (employeeService.updateEmployee(buildEntity())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee updated successfully!");
                clearFields(); loadEmployees();
            } else showAlert(Alert.AlertType.ERROR, "Error", "Employee not found!");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage()); }
    }

    @FXML private void deleteEmployee() {
        if (txtEmployeeId.getText().isEmpty()) { showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter Employee ID"); return; }
        try {
            if (employeeService.deleteEmployee(Integer.parseInt(txtEmployeeId.getText()))) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee deleted successfully!");
                clearFields(); loadEmployees();
            } else showAlert(Alert.AlertType.ERROR, "Error", "Employee not found!");
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage()); }
    }

    @FXML private void searchEmployee() {
        String empId = txtEmployeeId.getText();
        String empName = txtName.getText();
        if (empId.isEmpty() && empName.isEmpty()) { showAlert(Alert.AlertType.ERROR, "Validation Error", "Enter Employee ID or Name to search"); return; }
        try {
            Employee result = !empId.isEmpty()
                    ? employeeService.findById(Integer.parseInt(empId))
                    : employeeService.findByName(empName);
            if (result != null) {
                txtEmployeeId.setText(String.valueOf(result.getEmployeeId()));
                txtUserId.setText(String.valueOf(result.getUserId()));
                txtName.setText(result.getName()); txtRole.setText(result.getRole());
                txtContact.setText(result.getContact()); txtSalary.setText(String.valueOf(result.getSalary()));
                showAlert(Alert.AlertType.INFORMATION, "Found", "Employee found!");
            } else showAlert(Alert.AlertType.ERROR, "Not Found", "Employee not found!");
        } catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage()); }
    }

    @FXML private void btnBackOnAction(ActionEvent event) { navigateToDashboard(); }

    private void loadEmployees() {
        try { employeeList.setAll(employeeService.getAll()); }
        catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage()); }
    }

    private Employee buildEntity() {
        return new Employee(
                Integer.parseInt(txtEmployeeId.getText()), Integer.parseInt(txtUserId.getText()),
                txtName.getText(), txtRole.getText(), txtContact.getText(),
                Double.parseDouble(txtSalary.getText()));
    }

    private boolean validateFields() {
        if (txtEmployeeId.getText().isEmpty() || txtUserId.getText().isEmpty() ||
                txtName.getText().isEmpty() || txtRole.getText().isEmpty() ||
                txtContact.getText().isEmpty() || txtSalary.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all fields"); return false;
        }
        try { Double.parseDouble(txtSalary.getText().trim()); }
        catch (NumberFormatException e) { showAlert(Alert.AlertType.ERROR, "Validation Error", "Salary must be a valid number"); return false; }
        return true;
    }

    private void clearFields() {
        txtEmployeeId.clear(); txtUserId.clear(); txtName.clear();
        txtRole.clear(); txtContact.clear(); txtSalary.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    private void navigateToDashboard() {
        try {
            String role = UserSession.getUserRole();
            String path = role.equals("ADMIN") ? "/lk/ijse/wood_management/admindashboard.fxml" : "/lk/ijse/wood_management/empdashboard.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle(role.equals("ADMIN") ? "Admin Dashboard" : "Employee Dashboard");
            stage.centerOnScreen(); stage.setResizable(false);
        } catch (Exception e) { new Alert(Alert.AlertType.ERROR, "Cannot return to dashboard!").show(); }
    }
}
