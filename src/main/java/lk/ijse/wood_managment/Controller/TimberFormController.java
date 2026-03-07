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
import lk.ijse.wood_managment.Model.Timber;
import lk.ijse.wood_managment.db.DBConnection;
import lk.ijse.wood_managment.util.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TimberFormController {

    @FXML
    private TextField txtTimberId, txtCuttingId, txtQuantity, txtWidth, txtLength, txtSpecies;

    @FXML
    private TableView<Timber> tblTimber;

    @FXML
    private TableColumn<Timber, Integer> colTimberId, colCuttingId;

    @FXML
    private TableColumn<Timber, Double> colQuantity, colWidth, colLength;

    @FXML
    private TableColumn<Timber, String> colSpecies;

    @FXML
    private Button back;

    private final ObservableList<Timber> list = FXCollections.observableArrayList();


    public void initialize() {
        colTimberId.setCellValueFactory(new PropertyValueFactory<>("timberId"));
        colCuttingId.setCellValueFactory(new PropertyValueFactory<>("cuttingId"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colWidth.setCellValueFactory(new PropertyValueFactory<>("width"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("length"));
        colSpecies.setCellValueFactory(new PropertyValueFactory<>("species"));

        loadTable();

        tblTimber.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) fillFields(newVal);
                });
    }


    private void loadTable() {
        list.clear();
        try {
            Connection con = DBConnection.getInstance().getConnection();
            ResultSet rs = con.prepareStatement("SELECT * FROM timber").executeQuery();
            while (rs.next()) {
                list.add(new Timber(
                        rs.getInt("timber_id"),
                        rs.getInt("cutting_id"),
                        rs.getDouble("quantity"),
                        rs.getDouble("width"),
                        rs.getDouble("length"),
                        rs.getString("species")
                ));
            }
            tblTimber.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Timber> loadAllDetails() {
        List<Timber> details = new ArrayList<>();
        try {
            Connection con = DBConnection.getInstance().getConnection();
            ResultSet rs = con.prepareStatement("SELECT * FROM timber").executeQuery();
            while (rs.next()) {
                details.add(new Timber(
                        rs.getInt("timber_id"),
                        rs.getDouble("width"),
                        rs.getDouble("length"),
                        rs.getString("species")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }


    public boolean isStockAvailable(int timberId, double requiredQty) {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps =
                    con.prepareStatement("SELECT quantity FROM timber WHERE timber_id=?");
            ps.setInt(1, timberId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("quantity") >= requiredQty;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean updatetimber(int timberId, double qty) {
        try {
            if (!isStockAvailable(timberId, qty)) {
                return false;
            }

            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps =
                    con.prepareStatement(
                            "UPDATE timber SET quantity = quantity - ? WHERE timber_id=?");
            ps.setDouble(1, qty);
            ps.setInt(2, timberId);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Stock Update Failed: " + e.getMessage()).show();
            return false;
        }
    }


    private void fillFields(Timber t) {
        txtTimberId.setText(String.valueOf(t.getTimberId()));
        txtCuttingId.setText(String.valueOf(t.getCuttingId()));
        txtQuantity.setText(String.valueOf(t.getQuantity()));
        txtWidth.setText(String.valueOf(t.getWidth()));
        txtLength.setText(String.valueOf(t.getLength()));
        txtSpecies.setText(t.getSpecies());
    }

    private void clearFields() {
        txtTimberId.clear();
        txtCuttingId.clear();
        txtQuantity.clear();
        txtWidth.clear();
        txtLength.clear();
        txtSpecies.clear();
        tblTimber.getSelectionModel().clearSelection();
    }

    @FXML
    void btnAdd(ActionEvent event) {


        if (txtTimberId.getText().isEmpty() || txtCuttingId.getText().isEmpty() ||
                txtQuantity.getText().isEmpty() || txtWidth.getText().isEmpty() ||
                txtLength.getText().isEmpty() || txtSpecies.getText().isEmpty()) {

            new Alert(Alert.AlertType.WARNING,
                    "Please fill all fields before adding timber.").show();
            return;
        }

        try {
            int timberId = Integer.parseInt(txtTimberId.getText());
            int cuttingId = Integer.parseInt(txtCuttingId.getText());
            double newQty = Double.parseDouble(txtQuantity.getText());
            double width = Double.parseDouble(txtWidth.getText());
            double length = Double.parseDouble(txtLength.getText());
            String species = txtSpecies.getText();

            Connection con = DBConnection.getInstance().getConnection();

            PreparedStatement checkPs = con.prepareStatement(
                    "SELECT quantity FROM timber " +
                            "WHERE timber_id=? AND width=? AND length=? AND species=?"
            );
            checkPs.setInt(1, timberId);
            checkPs.setDouble(2, width);
            checkPs.setDouble(3, length);
            checkPs.setString(4, species);

            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                PreparedStatement updatePs = con.prepareStatement(
                        "UPDATE timber SET quantity = quantity + ? " +
                                "WHERE timber_id=? AND width=? AND length=? AND species=?"
                );
                updatePs.setDouble(1, newQty);
                updatePs.setInt(2, timberId);
                updatePs.setDouble(3, width);
                updatePs.setDouble(4, length);
                updatePs.setString(5, species);
                updatePs.executeUpdate();

                new Alert(Alert.AlertType.INFORMATION,
                        "Same Wood Found.\nQuantity Updated Successfully.").show();

            } else {
                PreparedStatement insertPs = con.prepareStatement(
                        "INSERT INTO timber VALUES (?,?,?,?,?,?)"
                );
                insertPs.setInt(1, timberId);
                insertPs.setInt(2, cuttingId);
                insertPs.setDouble(3, newQty);
                insertPs.setDouble(4, width);
                insertPs.setDouble(5, length);
                insertPs.setString(6, species);

                insertPs.executeUpdate();

                new Alert(Alert.AlertType.INFORMATION,
                        "New Wood Size Added Successfully.").show();
            }

            loadTable();
            clearFields();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING,
                    "Please enter valid numbers for:\n" +
                            "- Timber ID\n" +
                            "- Cutting ID\n" +
                            "- Quantity\n" +
                            "- Width\n" +
                            "- Length").show();

        } catch (Exception e) {

            new Alert(Alert.AlertType.ERROR,
                    "Cannot add timber.\nPlease check input values or try again.").show();
        }
    }


    @FXML
    void btnUpdate(ActionEvent event) {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE timber SET cutting_id=?, quantity=?, width=?, length=?, species=? WHERE timber_id=?");
            ps.setInt(1, Integer.parseInt(txtCuttingId.getText()));
            ps.setDouble(2, Double.parseDouble(txtQuantity.getText()));
            ps.setDouble(3, Double.parseDouble(txtWidth.getText()));
            ps.setDouble(4, Double.parseDouble(txtLength.getText()));
            ps.setString(5, txtSpecies.getText());
            ps.setInt(6, Integer.parseInt(txtTimberId.getText()));
            ps.executeUpdate();

            new Alert(Alert.AlertType.INFORMATION,
                    "Updated Successfully").show();
            loadTable();
            clearFields();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Update Failed: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnDelete(ActionEvent event) {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement ps =
                    con.prepareStatement("DELETE FROM timber WHERE timber_id=?");
            ps.setInt(1, Integer.parseInt(txtTimberId.getText()));
            ps.executeUpdate();

            new Alert(Alert.AlertType.INFORMATION,
                    "Deleted Successfully").show();
            loadTable();
            clearFields();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Delete Failed: " + e.getMessage()).show();
        }
    }
    @FXML
    void btnSearch(ActionEvent event) {
        try {
            Connection con = DBConnection.getInstance().getConnection();

            StringBuilder query = new StringBuilder("SELECT * FROM timber WHERE 1=1");
            List<Object> params = new ArrayList<>();

            if (!txtTimberId.getText().isEmpty()) {
                query.append(" AND timber_id=?");
                params.add(Integer.parseInt(txtTimberId.getText()));
            }
            if (!txtWidth.getText().isEmpty()) {
                query.append(" AND width=?");
                params.add(Double.parseDouble(txtWidth.getText()));
            }
            if (!txtLength.getText().isEmpty()) {
                query.append(" AND length=?");
                params.add(Double.parseDouble(txtLength.getText()));
            }
            if (!txtQuantity.getText().isEmpty()) {
                query.append(" AND quantity=?");
                params.add(Double.parseDouble(txtQuantity.getText()));
            }
            if (!txtSpecies.getText().isEmpty()) {
                query.append(" AND species LIKE ?");
                params.add(txtSpecies.getText());
            }

            PreparedStatement ps = con.prepareStatement(query.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            list.clear();
            boolean found = false;

            while (rs.next()) {
                found = true;
                list.add(new Timber(
                        rs.getInt("timber_id"),
                        rs.getInt("cutting_id"),
                        rs.getDouble("quantity"),
                        rs.getDouble("width"),
                        rs.getDouble("length"),
                        rs.getString("species")
                ));
            }

            tblTimber.setItems(list);

            if (!found) {
                new Alert(Alert.AlertType.WARNING, "No matching records found.").show();
            }

            clearFields();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Please enter valid numbers for numeric fields.").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Search Failed: " + e.getMessage()).show();
        }
    }

    @FXML
    private void btnback(ActionEvent event) {
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

    @FXML
    void btnClear(ActionEvent event) {
        clearFields();
    }
}
