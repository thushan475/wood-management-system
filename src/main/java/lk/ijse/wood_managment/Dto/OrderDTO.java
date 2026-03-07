    package lk.ijse.wood_managment.Dto;

    import java.time.LocalDate;

    public class OrderDTO {
        private int orderId;
        private int customerId;
        private LocalDate orderDate;
        private double totalAmount;
        private String description;
        private int timberId;
        private double width;
        private double length;
        private String species;
        private double quantity;

        public OrderDTO(int orderId, int customerId, LocalDate orderDate, double totalAmount,
                        String description, int timberId, double width, double length,
                        String species, double quantity) {
            this.orderId = orderId;
            this.customerId = customerId;
            this.orderDate = orderDate;
            this.totalAmount = totalAmount;
            this.description = description;
            this.timberId = timberId;
            this.width = width;
            this.length = length;
            this.species = species;
            this.quantity = quantity;
        }


        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public int getCustomerId() {
            return customerId;
        }

        public void setCustomerId(int customerId) {
            this.customerId = customerId;
        }

        public LocalDate getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(LocalDate orderDate) {
            this.orderDate = orderDate;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getTimberId() {
            return timberId;
        }

        public void setTimberId(int timberId) {
            this.timberId = timberId;
        }

        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public double getLength() {
            return length;
        }

        public void setLength(double length) {
            this.length = length;
        }

        public String getSpecies() {
            return species;
        }

        public void setSpecies(String species) {
            this.species = species;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "OrderDTO{" +
                    "orderId=" + orderId +
                    ", customerId=" + customerId +
                    ", orderDate=" + orderDate +
                    ", totalAmount=" + totalAmount +
                    ", description='" + description + '\'' +
                    ", timberId=" + timberId +
                    ", width=" + width +
                    ", length=" + length +
                    ", species='" + species + '\'' +
                    ", quantity=" + quantity +
                    '}';
        }
    }