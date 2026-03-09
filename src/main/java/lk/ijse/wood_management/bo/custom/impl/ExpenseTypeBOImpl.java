package lk.ijse.wood_management.bo.custom.impl;

import lk.ijse.wood_management.bo.custom.ExpenseTypeBO;

import lk.ijse.wood_management.dto.ExpenseTypeDTO;
import lk.ijse.wood_management.dao.custom.impl.ExpenseTypeDAOImpl;

import java.sql.SQLException;
import java.util.List;

public class ExpenseTypeBOImpl implements ExpenseTypeBO {

    private final ExpenseTypeDAOImpl expenseTypeRepo = new ExpenseTypeDAOImpl();

    public List<ExpenseTypeDTO> getAll() throws SQLException { return expenseTypeRepo.getAll(); }
    public ExpenseTypeDTO findById(int id) throws SQLException { return expenseTypeRepo.findById(id); }
    public boolean add(ExpenseTypeDTO dto) throws SQLException { return expenseTypeRepo.save(dto); }
    public boolean update(ExpenseTypeDTO dto) throws SQLException { return expenseTypeRepo.update(dto); }
    public boolean delete(int id) throws SQLException { return expenseTypeRepo.delete(id); }
}
