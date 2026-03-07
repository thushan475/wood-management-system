    package lk.ijse.wood_managment.Dto;

    import java.time.LocalDate;

    public class ExpenseDTO {
        private Integer expenseId;
        private Integer woodId;
        private Integer expenseTypeId;
        private String expenseType;
        private String description;
        private String species;
        private Double length;
        private Double width;
        private Double amount;
        private Double qtyPrice;
        private LocalDate expenseDate;

        public ExpenseDTO() {}

        public ExpenseDTO(Integer expenseId, Integer woodId, String expenseType, String description,
                          String species, Double length, Double width, Double qtyPrice,
                          LocalDate expenseDate, Double amount) {
            this.expenseId = expenseId;
            this.woodId = woodId;
            this.expenseType = expenseType;
            this.description = description;
            this.species = species;
            this.length = length;
            this.width = width;
            this.qtyPrice = qtyPrice;
            this.expenseDate = expenseDate;
            this.amount = amount;
        }


        public ExpenseDTO(String expenseType, String description, Double amount, LocalDate date) {
            this.expenseType = expenseType;
            this.description = description;
            this.amount = amount;
            this.expenseDate = date;
        }


        public Integer getExpenseId() { return expenseId; }
        public void setExpenseId(Integer expenseId) { this.expenseId = expenseId; }

        public Integer getWoodId() { return woodId; }
        public void setWoodId(Integer woodId) { this.woodId = woodId; }

        public Integer getExpenseTypeId() { return expenseTypeId; }
        public void setExpenseTypeId(Integer expenseTypeId) { this.expenseTypeId = expenseTypeId; }

        public String getExpenseType() { return expenseType; }
        public void setExpenseType(String expenseType) { this.expenseType = expenseType; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getSpecies() { return species; }
        public void setSpecies(String species) { this.species = species; }

        public Double getLength() { return length; }
        public void setLength(Double length) { this.length = length; }

        public Double getWidth() { return width; }
        public void setWidth(Double width) { this.width = width; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public Double getQtyPrice() { return qtyPrice; }
        public void setQtyPrice(Double qtyPrice) { this.qtyPrice = qtyPrice; }

        public LocalDate getExpenseDate() { return expenseDate; }
        public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    }
