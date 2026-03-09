package lk.ijse.wood_management.bo.custom.impl;

import lk.ijse.wood_management.bo.custom.ExpenseBO;

import lk.ijse.wood_management.dto.ExpenseDTO;
import lk.ijse.wood_management.dao.custom.impl.ExpenseDAOImpl;

import java.sql.SQLException;
import java.util.List;

public class ExpenseBOImpl implements ExpenseBO {

    private final ExpenseDAOImpl expenseRepo = new ExpenseDAOImpl();

    public List<ExpenseDTO> getAllExpenses() throws SQLException { return expenseRepo.getAll(); }
    public ExpenseDTO findById(int id) throws SQLException { return expenseRepo.findById(id); }
    public boolean addExpense(ExpenseDTO dto) throws SQLException { return expenseRepo.save(dto); }
    public boolean updateExpense(ExpenseDTO dto) throws SQLException { return expenseRepo.update(dto); }
    public boolean deleteExpense(int id) throws SQLException { return expenseRepo.delete(id); }
}
