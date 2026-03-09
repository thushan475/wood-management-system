package lk.ijse.wood_management.controller;

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
import lk.ijse.wood_management.dto.OrderDTO;
import lk.ijse.wood_management.bo.custom.impl.OrderBOImpl;
import lk.ijse.wood_management.bo.custom.impl.TimberBOImpl;
import lk.ijse.wood_management.util.UserSession;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

public class OrderFormController {

    @FXML private TextField txtOrderId, txtQuantity, txtTotalAmount, txtDescription;
    @FXML private DatePicker dpOrderDate;
    @FXML private ComboBox<Integer> customer_details, timber_details;
    @FXML private ComboBox<BigDecimal> width_details, length_details;
    @FXML private ComboBox<String> species_details;
    @FXML private TableView<OrderDTO> tblOrders;
    @FXML private TableColumn<OrderDTO, Integer> colOrderId, colCustomerId, colTimberId;
    @FXML private TableColumn<OrderDTO, LocalDate> colOrderDate;
    @FXML private TableColumn<OrderDTO, Double> colTotalAmount, colTotalQuantity, colWidth, colLength;
    @FXML private TableColumn<OrderDTO, String> colDescription, colSpecies;
    @FXML private Button back;

    private final OrderBOImpl orderService = new OrderBOImpl();
    private final TimberBOImpl timberService = new TimberBOImpl();
    private final ObservableList<OrderDTO> tempTimberList = FXCollections.observableArrayList();
    private boolean isEditMode = false;
    private boolean isCartMode = false;

    @FXML
    public void initialize() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colTotalQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTimberId.setCellValueFactory(new PropertyValueFactory<>("timberId"));
        colWidth.setCellValueFactory(new PropertyValueFactory<>("width"));
        colLength.setCellValueFactory(new PropertyValueFactory<>("length"));
        colSpecies.setCellValueFactory(new PropertyValueFactory<>("species"));
        tblOrders.setItems(tempTimberList);
        loadCustomerIds();
        loadTimberDetails();
        loadAllOrders();
    }

    private void loadCustomerIds() {
        try {
            customer_details.setItems(FXCollections.observableArrayList(orderService.getAllCustomerIds()));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load customers: " + e.getMessage());
        }
    }

    private void loadTimberDetails() {
        try {
            timber_details.setItems(FXCollections.observableArrayList(timberService.getAllTimberIds()));
            width_details.setItems(FXCollections.observableArrayList(timberService.getAllWidths()));
            length_details.setItems(FXCollections.observableArrayList(timberService.getAllLengths()));
            species_details.setItems(FXCollections.observableArrayList(timberService.getAllSpecies()));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load timber: " + e.getMessage());
        }
    }

    private void loadAllOrders() {
        try {
            tempTimberList.setAll(orderService.getAllOrders());
            isCartMode = false;
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load orders: " + e.getMessage());
        }
    }

    @FXML
    private void addTimberToOrder(ActionEvent ignored) {
        if (!isCartMode) {
            tempTimberList.clear();
            isCartMode = true;
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

            double availableStock = timberService.getAvailableStock(timberId, width, length, species);
            double alreadyAdded = tempTimberList.stream()
                    .filter(t -> t.getTimberId() == timberId
                            && toBD(t.getWidth()).compareTo(width) == 0
                            && toBD(t.getLength()).compareTo(length) == 0
                            && t.getSpecies().equals(species))
                    .mapToDouble(OrderDTO::getQuantity).sum();

            if (availableStock < alreadyAdded + qty) {
                showAlert(Alert.AlertType.ERROR, "Insufficient Stock",
                        String.format("Not enough! Available: %.2f | In cart: %.2f | Requested: %.2f",
                                availableStock, alreadyAdded, qty));
                return;
            }

            boolean updated = false;
            for (OrderDTO item : tempTimberList) {
                if (item.getTimberId() == timberId
                        && toBD(item.getWidth()).compareTo(width) == 0
                        && toBD(item.getLength()).compareTo(length) == 0
                        && item.getSpecies().equals(species)) {
                    item.setQuantity(item.getQuantity() + qty);
                    item.setTotalAmount(item.getTotalAmount() + amount);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                tempTimberList.add(new OrderDTO(0, customer_details.getValue(), dpOrderDate.getValue(),
                        amount, txtDescription.getText() == null ? "" : txtDescription.getText().trim(),
                        timberId, width.doubleValue(), length.doubleValue(), species, qty));
            }
            tblOrders.refresh();
            clearTimberFields();
            showAlert(Alert.AlertType.INFORMATION, "Added", updated ? "Quantity updated!" : "Item added to cart!");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Enter valid numbers!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add item: " + e.getMessage());
        }
    }

    @FXML
    private void submitOrder(ActionEvent ignored) {
        if (tempTimberList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Cart", "Add items first!");
            return;
        }
        if (!isCartMode) {
            showAlert(Alert.AlertType.WARNING, "Invalid Action", "Click 'New Order' and add items first!");
            return;
        }
        try {
            int orderId = orderService.submitOrder(
                    customer_details.getValue(), dpOrderDate.getValue(),
                    txtDescription.getText() == null ? "" : txtDescription.getText().trim(),
                    tempTimberList
            );
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Order saved!\nOrder ID: " + orderId + "\nTotal: Rs. " +
                            String.format("%.2f", tempTimberList.stream().mapToDouble(OrderDTO::getTotalAmount).sum()));
            clearInputFieldsOnly();
            loadTimberDetails();
            loadAllOrders();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed", "Order failed: " + e.getMessage());
        }
    }

    @FXML
    private void deleteOrder(ActionEvent ignored) {
        if (!isEditMode || txtOrderId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Search an order first!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this order?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        try {
            orderService.deleteOrder(Integer.parseInt(txtOrderId.getText().trim()));
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Order deleted and stock restored.");
            clearAllAndReload();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed", e.getMessage());
        }
    }

    @FXML
    private void searchOrder(ActionEvent ignored) {
        if (txtOrderId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Enter Order ID!");
            return;
        }
        try {
            List<OrderDTO> result = orderService.getOrderById(Integer.parseInt(txtOrderId.getText().trim()));
            if (result.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Not Found", "No order found!");
                clearInputFields();
            } else {
                tempTimberList.setAll(result);
                isEditMode = true;
                isCartMode = false;
                tblOrders.refresh();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid", "Enter valid number!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void clearFields(ActionEvent ignored) {
        clearInputFields();
        loadAllOrders();
    }

    @FXML
    private void newOrder(ActionEvent ignored) {
        clearInputFields();
        tempTimberList.clear();
        isCartMode = true;
    }

    @FXML
    private void refreshOrders(ActionEvent ignored) {
        loadAllOrders();
    }

    @FXML
    private void billInvoice(ActionEvent ignored) {
        if (txtOrderId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Required", "Enter Order ID!");
            return;
        }
        try {
            int orderId = Integer.parseInt(txtOrderId.getText().trim());
            JasperViewer.viewReport(orderService.generateInvoice(orderId), false);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid", "Enter valid Order ID!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed", e.getMessage());
        }
    }

    @FXML
    private void onTableRowClick(MouseEvent ignored) {
        OrderDTO selected = tblOrders.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        txtOrderId.setText(String.valueOf(selected.getOrderId()));
        customer_details.setValue(selected.getCustomerId());
        dpOrderDate.setValue(selected.getOrderDate());
        txtDescription.setText(selected.getDescription());
        timber_details.setValue(selected.getTimberId());
        width_details.setValue(toBD(selected.getWidth()));
        length_details.setValue(toBD(selected.getLength()));
        species_details.setValue(selected.getSpecies());
        txtQuantity.setText(String.valueOf(selected.getQuantity()));
        txtTotalAmount.setText(String.valueOf(selected.getTotalAmount()));
        isEditMode = true;
    }

    @FXML
    private void backToDashboard(ActionEvent ignored) {
        navigateToDashboard();
    }

    private BigDecimal toBD(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP);
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
        isCartMode = false;
    }

    private void clearInputFieldsOnly() {
        txtOrderId.clear();
        customer_details.setValue(null);
        dpOrderDate.setValue(null);
        txtDescription.clear();
        clearTimberFields();
        isEditMode = false;
        isCartMode = false;
    }

    private void clearAllAndReload() {
        clearInputFields();
        loadTimberDetails();
        loadAllOrders();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void navigateToDashboard() {
        try {
            String role = UserSession.getUserRole();
            String fxmlPath = role.equals("ADMIN")
                    ? "/lk/ijse/wood_management/admindashboard.fxml"
                    : "/lk/ijse/wood_management/empdashboard.fxml";
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Dashboard FXML not found!");
                return;
            }
            Parent root = FXMLLoader.load(resource);
            Stage stage = (Stage) back.getScene().getWindow();
            stage.setScene(new Scene(root, 1104, 622));
            stage.setTitle(role.equals("ADMIN") ? "Admin Dashboard" : "Employee Dashboard");
            stage.centerOnScreen();
            stage.setResizable(false);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load dashboard!");
        }
    }
}