package lk.ijse.wood_managment.Dto;

public class ExpenseTypeDTO {

    private int expenseId;
    private String expenseName;

    public ExpenseTypeDTO() {
    }

    public ExpenseTypeDTO(int expenseId, String expenseName) {
        this.expenseId = expenseId;
        this.expenseName = expenseName;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }
}
