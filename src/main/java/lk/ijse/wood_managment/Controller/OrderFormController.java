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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lk.ijse.wood_managment.Dto.OrderDTO;
import lk.ijse.wood_managment.db.DBConnection;
import lk.ijse.wood_managment.util.UserSession;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class OrderFormController {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/wood_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "mysql";

    @FXML private TextField txtOrderId, txtQuantity, txtTotalAmount, txtDescription;
    @FXML private DatePicker dpOrderDate;
    @FXML private ComboBox<Integer> customer_details, timber_details;
    @FXML private ComboBox<BigDecimal> width_details, length_details;
    @FXML private ComboBox<String> species_details;
    @FXML private TableView<OrderDTO> tblOrders;
    @FXML private TableColumn<OrderDTO, Integer> colOrderId, colCustomerId, colTimberId;
    @FXML private TableColumn<OrderDTO, LocalDate> colOrderDate;
    @FXML private TableColumn<OrderDTO, Double> colTotalAmount, colTotalquantity, colWidth, colLength;
    @FXML private TableColumn<OrderDTO, String> colDescription, colSpecies;
    @FXML private Button back;

    private final ObservableList<OrderDTO> tempTimberList = FXCollections.observableArrayList();
    private boolean isEditMode = false;
    private boolean isNewOrderMode = true; // Track whether creating new order or viewing existing

    @FXML
    public void initialize() {
        setupTableColumns();
        tblOrders.setItems(tempTimberList);
        loadCustomerIds();
        loadTimberDetails();
        loadAllOrders(); // Load all orders when form opens
    }

    private void setupTableColumns() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colTotalquantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTimberId.setCellValueFactory(new PropertyValueFactory<>("timberId"));
        colWidth.setCellValueFactory(new PropertyValueFactory<>("width"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("length"));
        colSpecies.setCellValueFactory(new PropertyValueFactory<>("species"));
    }

    private void loadCustomerIds() {
        ObservableList<Integer> customerList = FXCollections.observableArrayList();
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT customer_id FROM customer ORDER BY customer_id")) {
            while (rs.next()) {
                customerList.add(rs.getInt("customer_id"));
            }
            customer_details.setItems(customerList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load customers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTimberDetails() {
        ObservableList<Integer> timberIds = FXCollections.observableArrayList();
        ObservableList<BigDecimal> widths = FXCollections.observableArrayList();
        ObservableList<BigDecimal> lengths = FXCollections.observableArrayList();
        ObservableList<String> speciesList = FXCollections.observableArrayList();

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT timber_id, width, length, species FROM timber WHERE quantity > 0 ORDER BY timber_id")) {
            while (rs.next()) {
                int timberId = rs.getInt("timber_id");
                BigDecimal width = rs.getBigDecimal("width").setScale(2, RoundingMode.HALF_UP);
                BigDecimal length = rs.getBigDecimal("length").setScale(2, RoundingMode.HALF_UP);
                String species = rs.getString("species");

                if (!timberIds.contains(timberId)) timberIds.add(timberId);
                if (widths.stream().noneMatch(w -> w.compareTo(width) == 0)) widths.add(width);
                if (lengths.stream().noneMatch(l -> l.compareTo(length) == 0)) lengths.add(length);
                if (!speciesList.contains(species)) speciesList.add(species);
            }
            timber_details.setItems(timberIds);
            width_details.setItems(widths);
            length_details.setItems(lengths);
            species_details.setItems(speciesList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load timber details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // NEW METHOD: Load all orders from database
    private void loadAllOrders() {
        tempTimberList.clear();
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT co.order_id, co.customer_id, co.order_date, co.description, " +
                    "coi.timber_id, coi.width, coi.length, coi.species, coi.quantity, coi.amount " +
                    "FROM customer_order co " +
                    "JOIN customer_order_items coi ON co.order_id = coi.order_id " +
                    "ORDER BY co.order_id DESC";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                tempTimberList.add(new OrderDTO(
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getDate("order_date").toLocalDate(),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getInt("timber_id"),
                        rs.getDouble("width"),
                        rs.getDouble("length"),
                        rs.getString("species"),
                        rs.getDouble("quantity")
                ));
            }

            tblOrders.refresh();
            isNewOrderMode = false;

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load orders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addTimberToOrder(ActionEvent event) {

        if (!isNewOrderMode) {
            tempTimberList.clear();
            isNewOrderMode = true;
        }

        if (customer_details.getValue() == null || dpOrderDate.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Select customer and order date first!");
            return;
        }
        if (timber_details.getValue() == null || width_details.getValue() == null ||
                length_details.getValue() == null || species_details.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Select all timber details!");
            return;
        }
        if (txtQuantity.getText().trim().isEmpty() || txtTotalAmount.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Enter quantity and amount!");
            return;
        }

        try {
            int timberId = timber_details.getValue();
            BigDecimal width = width_details.getValue();
            BigDecimal length = length_details.getValue();
            String species = species_details.getValue();
            double qty = Double.parseDouble(txtQuantity.getText().trim());
            double amount = Double.parseDouble(txtTotalAmount.getText().trim());
            String desc = txtDescription.getText() == null ? "" : txtDescription.getText().trim();

            if (qty <= 0 || amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Quantity and amount must be positive!");
                return;
            }

            double availableStock = getAvailableStock(timberId, width, length, species);
            if (availableStock <= 0) {
                showAlert(Alert.AlertType.ERROR, "Stock Error",
                        String.format("No timber found!\nID: %d, Width: %s, Length: %s, Species: %s",
                                timberId, width, length, species));
                return;
            }

            double alreadyAdded = tempTimberList.stream()
                    .filter(t -> t.getTimberId() == timberId &&
                            toBigDecimal(t.getWidth()).compareTo(width) == 0 &&
                            toBigDecimal(t.getLength()).compareTo(length) == 0 &&
                            t.getSpecies().equals(species))
                    .mapToDouble(OrderDTO::getQuantity)
                    .sum();

            if (availableStock < alreadyAdded + qty) {
                showAlert(Alert.AlertType.ERROR, "Insufficient Stock",
                        String.format("Not enough! Available: %.2f | In cart: %.2f | Requested: %.2f",
                                availableStock, alreadyAdded, qty));
                return;
            }

            boolean updated = false;
            for (OrderDTO item : tempTimberList) {
                if (item.getTimberId() == timberId &&
                        toBigDecimal(item.getWidth()).compareTo(width) == 0 &&
                        toBigDecimal(item.getLength()).compareTo(length) == 0 &&
                        item.getSpecies().equals(species)) {
                    item.setQuantity(item.getQuantity() + qty);
                    item.setTotalAmount(item.getTotalAmount() + amount);
                    updated = true;
                    showAlert(Alert.AlertType.INFORMATION, "Updated", "Quantity updated!");
                    break;
                }
            }

            if (!updated) {
                OrderDTO newItem = new OrderDTO(
                        0,
                        customer_details.getValue(),
                        dpOrderDate.getValue(),
                        amount,
                        desc,
                        timberId,
                        width.doubleValue(),
                        length.doubleValue(),
                        species,
                        qty
                );
                tempTimberList.add(newItem);
                showAlert(Alert.AlertType.INFORMATION, "Added", "Item added to cart!");
            }

            tblOrders.refresh();
            clearTimberFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Enter valid numbers!");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void submitOrder(ActionEvent event) {
        if (tempTimberList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Cart", "Add items first!");
            return;
        }

        // Check if we're in new order mode
        if (!isNewOrderMode) {
            showAlert(Alert.AlertType.WARNING, "Invalid Action", "Cannot submit existing orders. Clear and create new order!");
            return;
        }

        Connection con = null;
        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            con.setAutoCommit(false);

            double totalAmount = tempTimberList.stream().mapToDouble(OrderDTO::getTotalAmount).sum();

            String insertOrderSql = "INSERT INTO customer_order (customer_id, order_date, description, total_amount) VALUES (?,?,?,?)";
            PreparedStatement orderStmt = con.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, customer_details.getValue());
            orderStmt.setDate(2, Date.valueOf(dpOrderDate.getValue()));
            orderStmt.setString(3, txtDescription.getText() == null ? "" : txtDescription.getText().trim());
            orderStmt.setDouble(4, totalAmount);
            orderStmt.executeUpdate();

            ResultSet keys = orderStmt.getGeneratedKeys();
            int orderId = 0;
            if (keys.next()) {
                orderId = keys.getInt(1);
            }

            Map<String, OrderDTO> consolidated = new HashMap<>();
            for (OrderDTO item : tempTimberList) {
                String key = item.getTimberId() + "-" + toBigDecimal(item.getWidth()) + "-" +
                        toBigDecimal(item.getLength()) + "-" + item.getSpecies();
                if (consolidated.containsKey(key)) {
                    OrderDTO existing = consolidated.get(key);
                    existing.setQuantity(existing.getQuantity() + item.getQuantity());
                    existing.setTotalAmount(existing.getTotalAmount() + item.getTotalAmount());
                } else {
                    consolidated.put(key, new OrderDTO(
                            item.getOrderId(),
                            item.getCustomerId(),
                            item.getOrderDate(),
                            item.getTotalAmount(),
                            item.getDescription(),
                            item.getTimberId(),
                            item.getWidth(),
                            item.getLength(),
                            item.getSpecies(),
                            item.getQuantity()));
                }
            }

            for (OrderDTO item : consolidated.values()) {
                BigDecimal width = toBigDecimal(item.getWidth());
                BigDecimal length = toBigDecimal(item.getLength());

                double stock = getCurrentStock(con, item.getTimberId(), width, length, item.getSpecies());
                if (stock < item.getQuantity()) {
                    throw new SQLException("Insufficient stock for Timber " + item.getTimberId());
                }

                insertOrderItem(con, orderId, item, width, length);
                reduceStock(con, item.getTimberId(), width, length, item.getSpecies(), item.getQuantity());
            }

            con.commit();

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Order saved successfully!\nOrder ID: " + orderId + "\nTotal: Rs. " + String.format("%.2f", totalAmount));

            clearInputFieldsOnly();
            loadTimberDetails();
            loadAllOrders(); // Reload all orders to show the new one

        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            showAlert(Alert.AlertType.ERROR, "Failed", "Order failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void deleteOrder(ActionEvent event) {
        if (!isEditMode || txtOrderId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Search an order first!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this order?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;

        Connection con = null;
        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            con.setAutoCommit(false);
            int orderId = Integer.parseInt(txtOrderId.getText().trim());

            restoreStockForOrder(con, orderId);
            deleteOrderItems(con, orderId);
            deleteMainOrder(con, orderId);

            con.commit();
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Order deleted and stock restored.");
            clearAllAndReload();
        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            showAlert(Alert.AlertType.ERROR, "Failed", e.getMessage());
            e.printStackTrace();
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    @FXML
    private void searchOrder(ActionEvent event) {
        if (txtOrderId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Enter Order ID!");
            return;
        }
        try {
            loadOrderById(Integer.parseInt(txtOrderId.getText().trim()));
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid", "Enter valid number!");
        }
    }

    @FXML
    private void clearFields(ActionEvent event) {
        clearInputFields();
        loadAllOrders(); // Reload all orders from database
        showAlert(Alert.AlertType.INFORMATION, "Cleared", "Ready for new order.");
    }

    // NEW METHOD: Button to create new order
    @FXML
    private void newOrder(ActionEvent event) {
        clearInputFields();
        tempTimberList.clear();
        isNewOrderMode = true;
        showAlert(Alert.AlertType.INFORMATION, "New Order", "Ready to create new order!");
    }

    // NEW METHOD: Button to refresh/view all orders
    @FXML
    private void refreshOrders(ActionEvent event) {
        loadAllOrders();
        showAlert(Alert.AlertType.INFORMATION, "Refreshed", "Orders loaded from database.");
    }

    @FXML
    void billinvoice(ActionEvent event) {
        if (txtOrderId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Required", "Enter Order ID!");
            return;
        }
        try {
            int orderId = Integer.parseInt(txtOrderId.getText().trim());
            Connection con = DBConnection.getInstance().getConnection();
            InputStream stream = getClass().getResourceAsStream("/reports/billInvoice.jrxml");
            if (stream == null) {
                showAlert(Alert.AlertType.ERROR, "Not Found", "Report file missing!");
                return;
            }
            JasperReport report = JasperCompileManager.compileReport(stream);
            Map<String, Object> params = new HashMap<>();
            params.put("ORDER_ID", orderId);
            JasperPrint print = JasperFillManager.fillReport(report, params, con);
            JasperViewer.viewReport(print, false);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void click(MouseEvent event) {
        OrderDTO selected = tblOrders.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        txtOrderId.setText(String.valueOf(selected.getOrderId()));
        customer_details.setValue(selected.getCustomerId());
        dpOrderDate.setValue(selected.getOrderDate());
        txtDescription.setText(selected.getDescription());
        timber_details.setValue(selected.getTimberId());
        width_details.setValue(toBigDecimal(selected.getWidth()));
        length_details.setValue(toBigDecimal(selected.getLength()));
        species_details.setValue(selected.getSpecies());
        txtQuantity.setText(String.valueOf(selected.getQuantity()));
        txtTotalAmount.setText(String.valueOf(selected.getTotalAmount()));
        isEditMode = true;
    }

    @FXML
    void backTodashboard(ActionEvent event) {
        try {
            String role = UserSession.getUserRole();
            String path = role.equals("ADMIN") ? "/lk/ijse/wood_managment/admindashboard.fxml"
                    : "/lk/ijse/wood_managment/empdashboard.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(path));
            Stage stage = (Stage) back.getScene().getWindow();
            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle(role.equals("ADMIN") ? "Admin Dashboard" : "Employee Dashboard");
            stage.centerOnScreen();
            stage.setResizable(false);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load dashboard!");
            e.printStackTrace();
        }
    }

    private BigDecimal toBigDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private double getAvailableStock(int timberId, BigDecimal width, BigDecimal length, String species) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pst = con.prepareStatement(
                     "SELECT quantity FROM timber WHERE timber_id=? AND width=? AND length=? AND species=?")) {
            pst.setInt(1, timberId);
            pst.setBigDecimal(2, width.setScale(2, RoundingMode.HALF_UP));
            pst.setBigDecimal(3, length.setScale(2, RoundingMode.HALF_UP));
            pst.setString(4, species);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? rs.getDouble("quantity") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private double getCurrentStock(Connection con, int timberId, BigDecimal width, BigDecimal length, String species) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement(
                "SELECT quantity FROM timber WHERE timber_id=? AND width=? AND length=? AND species=?")) {
            pst.setInt(1, timberId);
            pst.setBigDecimal(2, width.setScale(2, RoundingMode.HALF_UP));
            pst.setBigDecimal(3, length.setScale(2, RoundingMode.HALF_UP));
            pst.setString(4, species);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? rs.getDouble("quantity") : 0;
        }
    }

    private void insertOrderItem(Connection con, int orderId, OrderDTO item, BigDecimal width, BigDecimal length) throws SQLException {
        String sql = "INSERT INTO customer_order_items (order_id,timber_id,width,length,species,quantity,amount) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, orderId);
            pst.setInt(2, item.getTimberId());
            pst.setBigDecimal(3, width.setScale(2, RoundingMode.HALF_UP));
            pst.setBigDecimal(4, length.setScale(2, RoundingMode.HALF_UP));
            pst.setString(5, item.getSpecies());
            pst.setDouble(6, item.getQuantity());
            pst.setDouble(7, item.getTotalAmount());
            pst.executeUpdate();
        }
    }

    private void reduceStock(Connection con, int timberId, BigDecimal width, BigDecimal length, String species, double qty) throws SQLException {
        String sql = "UPDATE timber SET quantity=quantity-? WHERE timber_id=? AND width=? AND length=? AND species=?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDouble(1, qty);
            pst.setInt(2, timberId);
            pst.setBigDecimal(3, width.setScale(2, RoundingMode.HALF_UP));
            pst.setBigDecimal(4, length.setScale(2, RoundingMode.HALF_UP));
            pst.setString(5, species);
            if (pst.executeUpdate() == 0) {
                throw new SQLException("Failed to reduce stock!");
            }
        }
    }

    private void restoreStockForOrder(Connection con, int orderId) throws SQLException {
        String sql = "UPDATE timber t JOIN customer_order_items oi ON t.timber_id=oi.timber_id " +
                "AND t.width=oi.width AND t.length=oi.length AND t.species=oi.species " +
                "SET t.quantity=t.quantity+oi.quantity WHERE oi.order_id=?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, orderId);
            pst.executeUpdate();
        }
    }

    private void deleteOrderItems(Connection con, int orderId) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement("DELETE FROM customer_order_items WHERE order_id=?")) {
            pst.setInt(1, orderId);
            pst.executeUpdate();
        }
    }

    private void deleteMainOrder(Connection con, int orderId) throws SQLException {
        try (PreparedStatement pst = con.prepareStatement("DELETE FROM customer_order WHERE order_id=?")) {
            pst.setInt(1, orderId);
            pst.executeUpdate();
        }
    }

    private void loadOrderById(int orderId) {
        tempTimberList.clear();
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            PreparedStatement orderStmt = con.prepareStatement("SELECT * FROM customer_order WHERE order_id=?");
            orderStmt.setInt(1, orderId);
            ResultSet orderRs = orderStmt.executeQuery();

            if (orderRs.next()) {
                customer_details.setValue(orderRs.getInt("customer_id"));
                dpOrderDate.setValue(orderRs.getDate("order_date").toLocalDate());
                txtDescription.setText(orderRs.getString("description"));

                PreparedStatement itemsStmt = con.prepareStatement("SELECT * FROM customer_order_items WHERE order_id=?");
                itemsStmt.setInt(1, orderId);
                ResultSet itemsRs = itemsStmt.executeQuery();

                while (itemsRs.next()) {
                    tempTimberList.add(new OrderDTO(
                            orderId,
                            orderRs.getInt("customer_id"),
                            orderRs.getDate("order_date").toLocalDate(),
                            itemsRs.getDouble("amount"),
                            orderRs.getString("description"),
                            itemsRs.getInt("timber_id"),
                            itemsRs.getDouble("width"),
                            itemsRs.getDouble("length"),
                            itemsRs.getString("species"),
                            itemsRs.getDouble("quantity")));
                }
                isEditMode = true;
                isNewOrderMode = false;
                tblOrders.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Found", "Order loaded.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Not Found", "No order with ID: " + orderId);
                clearInputFields();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearTimberFields() {
        timber_details.setValue(null);
        width_details.setValue(null);
        length_details.setValue(null);
        species_details.setValue(null);
        txtQuantity.clear();
        txtTotalAmount.clear();
    }

    private void clearInputFields() {
        txtOrderId.clear();
        customer_details.setValue(null);
        dpOrderDate.setValue(null);
        txtDescription.clear();
        clearTimberFields();
        tempTimberList.clear();
        isEditMode = false;
        isNewOrderMode = true;
    }

    private void clearInputFieldsOnly() {
        txtOrderId.clear();
        customer_details.setValue(null);
        dpOrderDate.setValue(null);
        txtDescription.clear();
        clearTimberFields();
        isEditMode = false;
        isNewOrderMode = true;
    }

    private void clearAllAndReload() {
        clearInputFields();
        loadTimberDetails();
        loadAllOrders();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}