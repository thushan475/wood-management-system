package lk.ijse.wood_managment.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lk.ijse.wood_managment.Dto.SupplierDTO;
import lk.ijse.wood_managment.util.UserSession;

import java.io.IOException;
import java.sql.*;

public class SupplierFormController {

    @FXML
    private Button back;
    @FXML
    private TextField txtId, txtName, txtContact, txtdescription;

    @FXML
    private TableView<SupplierDTO> tblSupplier;
    @FXML
    private TableColumn<SupplierDTO, Integer> colId;
    @FXML
    private TableColumn<SupplierDTO, String> colName;
    @FXML
    private TableColumn<SupplierDTO, String> colContact;
    @FXML
    private TableColumn<SupplierDTO, String> coldescription;

    private Connection connection;

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        coldescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        connectDatabase();
        loadSuppliers();

        tblSupplier.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtId.setText(String.valueOf(newVal.getSupplierId()));
                txtName.setText(newVal.getName());
                txtContact.setText(newVal.getContactNumber());
                txtdescription.setText(newVal.getDescription());
            }
        });
    }

    private void connectDatabase() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/wood_management", "root", "mysql");
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "DB Connection Failed").show();
        }
    }

    private void loadSuppliers() {
        ObservableList<SupplierDTO> list = FXCollections.observableArrayList();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM supplier");
            while (rs.next()) {
                list.add(new SupplierDTO(
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        rs.getString("description")
                ));
            }
            tblSupplier.setItems(list);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void addSupplier(ActionEvent event) {
        try {
            PreparedStatement pst = connection.prepareStatement(
                    "INSERT INTO supplier VALUES (?,?,?,?)");
            pst.setInt(1, generateId());
            pst.setString(2, txtName.getText());
            pst.setString(3, txtContact.getText());
            pst.setString(4, txtdescription.getText());

            pst.executeUpdate();
            loadSuppliers();
            clearFields();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void updateSupplier(ActionEvent event) {
        SupplierDTO s = tblSupplier.getSelectionModel().getSelectedItem();
        if (s == null) return;

        try {
            PreparedStatement pst = connection.prepareStatement(
                    "UPDATE supplier SET name=?, contact_number=?, description=? WHERE supplier_id=?");
            pst.setString(1, txtName.getText());
            pst.setString(2, txtContact.getText());
            pst.setString(3, txtdescription.getText());
            pst.setInt(4, s.getSupplierId());

            pst.executeUpdate();
            loadSuppliers();
            clearFields();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void deleteSupplier(ActionEvent event) {
        SupplierDTO s = tblSupplier.getSelectionModel().getSelectedItem();
        if (s == null) return;

        try {
            PreparedStatement pst = connection.prepareStatement(
                    "DELETE FROM supplier WHERE supplier_id=?");
            pst.setInt(1, s.getSupplierId());

            pst.executeUpdate();
            loadSuppliers();
            clearFields();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void searchSupplier(ActionEvent event) {
        ObservableList<SupplierDTO> list = FXCollections.observableArrayList();
        try {
            PreparedStatement pst = connection.prepareStatement(
                    "SELECT * FROM supplier WHERE name LIKE ?");
            pst.setString(1, "%" + txtName.getText() + "%");

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new SupplierDTO(
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        rs.getString("description")
                ));
            }
            tblSupplier.setItems(list);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private int generateId() throws SQLException {
        ResultSet rs = connection.createStatement()
                .executeQuery("SELECT MAX(supplier_id) FROM supplier");
        if (rs.next()) return rs.getInt(1) + 1;
        return 1;
    }

    private void clearFields() {
        txtId.clear();
        txtName.clear();
        txtContact.clear();
        txtdescription.clear();
    }

    @FXML
    private void backAction(ActionEvent event) {
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
}
