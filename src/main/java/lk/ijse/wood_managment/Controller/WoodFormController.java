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
import lk.ijse.wood_managment.Model.Wood;
import lk.ijse.wood_managment.util.UserSession;

import java.sql.*;

public class WoodFormController {

    @FXML
    private ComboBox<Integer> cmbSupId;
    @FXML
    private TextField txtSpecies;
    @FXML
    private TextField txtLength;
    @FXML
    private TextField txtWidth;
    @FXML
    private TextField txtUnitPrice;
    @FXML
    private DatePicker dpPurchaseDate;

    @FXML
    private TableView<Wood> tblWood;
    @FXML
    private TableColumn<Wood, Integer> colWoodId;
    @FXML
    private TableColumn<Wood, Integer> colSupId;
    @FXML
    private TableColumn<Wood, String> colSpecies;
    @FXML
    private TableColumn<Wood, Date> colPurchaseDate;
    @FXML
    private TableColumn<Wood, Double> colLength;
    @FXML
    private TableColumn<Wood, Double> colWidth;
    @FXML
    private TableColumn<Wood, Double> colUnitPrice;
    @FXML
    private Button btnBack;

    private final String URL = "jdbc:mysql://localhost:3306/wood_management";
    private final String USER = "root";
    private final String PASSWORD = "mysql";

    @FXML
    public void initialize() {
        colWoodId.setCellValueFactory(new PropertyValueFactory<>("woodId"));
        colSupId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colSpecies.setCellValueFactory(new PropertyValueFactory<>("species"));
        colPurchaseDate.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("length"));
        colWidth.setCellValueFactory(new PropertyValueFactory<>("width"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));


        loadSuppliers();
        loadAllWood();
        tableRowClick();
    }

    private void loadSuppliers() {
        ObservableList<Integer> supIds = FXCollections.observableArrayList();
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT supplier_id FROM supplier")) {

            while (rs.next()) {
                supIds.add(rs.getInt("supplier_id"));
            }
            cmbSupId.setItems(supIds);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load supplier IDs: " + e.getMessage());
        }
    }

    @FXML
    private void addWood(ActionEvent event) {
        if (!validateFields()) return;

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {

            String sql = "INSERT INTO wood (supplier_id, species, length, width, purchaseDate, unitprice) VALUES (?,?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pst.setInt(1, cmbSupId.getValue());
            pst.setString(2, txtSpecies.getText());
            pst.setDouble(3, Double.parseDouble(txtLength.getText()));
            pst.setDouble(4, Double.parseDouble(txtWidth.getText()));
            pst.setDate(5, dpPurchaseDate.getValue() != null ? java.sql.Date.valueOf(dpPurchaseDate.getValue()) : null);
            pst.setDouble(6, Double.parseDouble(txtUnitPrice.getText()));

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Creating wood failed, no rows affected.");


            ResultSet generatedKeys = pst.getGeneratedKeys();
            int woodId = 0;
            if (generatedKeys.next()) {
                woodId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating wood failed, no ID obtained.");
            }


            double qtyPrice = Double.parseDouble(txtUnitPrice.getText());
            double length = Double.parseDouble(txtLength.getText());
            double width = Double.parseDouble(txtWidth.getText());
            double amount = (length * width * length) / 2304 * qtyPrice;

            String sql2 = "INSERT INTO expense (wood_id, expense_type, description, species, length, width, qty_price, amount, expense_date) " +
                    "VALUES (?,?,?,?,?,?,?,?,?)";
            PreparedStatement pst2 = con.prepareStatement(sql2);
            pst2.setInt(1, woodId);
            pst2.setString(2, "Buy Wood");
            pst2.setString(3, "Wood purchase");
            pst2.setString(4, txtSpecies.getText());
            pst2.setDouble(5, length);
            pst2.setDouble(6, width);
            pst2.setDouble(7, qtyPrice);
            pst2.setDouble(8, amount);
            pst2.setDate(9, java.sql.Date.valueOf(dpPurchaseDate.getValue()));
            pst2.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Wood and expense added successfully!");
            clearFields();
            loadAllWood();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }


    @FXML
    private void updateWood(ActionEvent event) {
        Wood wood = tblWood.getSelectionModel().getSelectedItem();
        if (wood == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Select a row first");
            return;
        }
        if (!validateFields()) return;

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "UPDATE wood SET supplier_id=?, species=?, length=?, width=?, purchaseDate=?, unitprice=? WHERE wood_id=?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, cmbSupId.getValue());
            pst.setString(2, txtSpecies.getText());
            pst.setDouble(3, Double.parseDouble(txtLength.getText()));
            pst.setDouble(4, Double.parseDouble(txtWidth.getText()));
            pst.setDate(5, dpPurchaseDate.getValue() != null ? java.sql.Date.valueOf(dpPurchaseDate.getValue()) : null);
            pst.setDouble(6, Double.parseDouble(txtUnitPrice.getText()));
            pst.setInt(7, wood.getWoodId());
            pst.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Update Successful");
            clearFields();
            loadAllWood();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void deleteWood(ActionEvent event) {
        Wood wood = tblWood.getSelectionModel().getSelectedItem();
        if (wood == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Select a row first");
            return;
        }

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement pst = con.prepareStatement("DELETE FROM wood WHERE wood_id=?");
            pst.setInt(1, wood.getWoodId());
            int rows = pst.executeUpdate();

            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Delete Successful");
                clearFields();
                loadAllWood();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Delete Failed");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void searchWood(ActionEvent event) {
        if (txtSpecies.getText().isEmpty() && cmbSupId.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Enter Species or select Supplier ID to search");
            return;
        }

        ObservableList<Wood> list = FXCollections.observableArrayList();
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM wood WHERE 1=1";
            if (!txtSpecies.getText().isEmpty()) sql += " AND species LIKE ?";
            if (cmbSupId.getValue() != null) sql += " AND supplier_id=?";

            PreparedStatement pst = con.prepareStatement(sql);
            int idx = 1;
            if (!txtSpecies.getText().isEmpty()) pst.setString(idx++, "%" + txtSpecies.getText() + "%");
            if (cmbSupId.getValue() != null) pst.setInt(idx, cmbSupId.getValue());

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new Wood(
                        rs.getInt("wood_id"),
                        rs.getInt("supplier_id"),
                        rs.getString("species"),
                        rs.getDate("purchaseDate"),
                        rs.getDouble("length"),
                        rs.getDouble("width"),
                        rs.getDouble("unitprice")
                ));
            }

            if (list.isEmpty()) showAlert(Alert.AlertType.INFORMATION, "No Data", "No Data Found");

            tblWood.setItems(list);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void loadAllWood() {
        ObservableList<Wood> list = FXCollections.observableArrayList();
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             ResultSet rs = con.createStatement().executeQuery("SELECT * FROM wood")) {

            while (rs.next()) {
                list.add(new Wood(
                        rs.getInt("wood_id"),
                        rs.getInt("supplier_id"),
                        rs.getString("species"),
                        rs.getDate("purchaseDate"),
                        rs.getDouble("length"),
                        rs.getDouble("width"),
                        rs.getDouble("unitprice")
                ));
            }

            tblWood.setItems(list);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void tableRowClick() {
        tblWood.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, wood) -> {
            if (wood != null) {
                cmbSupId.setValue(wood.getSupplierId());
                txtSpecies.setText(wood.getSpecies());
                txtLength.setText(String.valueOf(wood.getLength()));
                txtWidth.setText(String.valueOf(wood.getWidth()));
                txtUnitPrice.setText(String.valueOf(wood.getUnitPrice()));
                dpPurchaseDate.setValue(wood.getPurchaseDate() != null ? wood.getPurchaseDate().toLocalDate() : null);
            }
        });
    }

    private void clearFields() {
        cmbSupId.setValue(null);
        txtSpecies.clear();
        txtLength.clear();
        txtWidth.clear();
        txtUnitPrice.clear();
        dpPurchaseDate.setValue(null);
        tblWood.getSelectionModel().clearSelection();
    }

    private boolean validateFields() {
        if (cmbSupId.getValue() == null || txtSpecies.getText().isEmpty() ||
                txtLength.getText().isEmpty() || txtWidth.getText().isEmpty() ||
                txtUnitPrice.getText().isEmpty() || dpPurchaseDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill all fields");
            return false;
        }
        return true;
    }

    @FXML
    private void backToDashboard(ActionEvent event) {
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
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load dashboard");
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