package lk.ijse.wood_management.dao;

import lk.ijse.wood_management.dao.custom.impl.BillDAOImpl;
import lk.ijse.wood_management.dao.custom.impl.CustomerDAOImpl;
import lk.ijse.wood_management.dao.custom.impl.CuttingDAOImpl;
import lk.ijse.wood_management.dao.custom.impl.EmployeeDAOImpl;
import lk.ijse.wood_management.dao.custom.impl.ExpenseDAOImpl;
import lk.ijse.wood_management.dao.custom.impl.ExpenseTypeDAOImpl;
import lk.ijse.wood_management.dao.custom.impl.OrderDAOImpl;
import lk.ijse.wood_management.dao.custom.impl.SupplierDAOImpl;
import lk.ijse.wood_management.dao.custom.impl.TimberDAOImpl;
import lk.ijse.wood_management.dao.custom.impl.WoodDAOImpl;

public class DAOFactory {
    private static DAOFactory instance;
    private DAOFactory() {}

    public static DAOFactory getInstance() {
        return instance == null ? instance = new DAOFactory() : instance;
    }

    public enum DAOType {
        CUSTOMER, SUPPLIER, TIMBER, WOOD, CUTTING, ORDER, BILL, EXPENSE, EXPENSE_TYPE, EMPLOYEE
    }

    public SuperDAO getDAO(DAOType type) {
        switch (type) {
            case CUSTOMER:
                return new CustomerDAOImpl();
            case SUPPLIER:
                return new SupplierDAOImpl();
            case TIMBER:
                return new TimberDAOImpl();
            case WOOD:
                return new WoodDAOImpl();
            case CUTTING:
                return new CuttingDAOImpl();
            case ORDER:        return new OrderDAOImpl();
            case BILL:
                return new BillDAOImpl();
            case EXPENSE:
                return new ExpenseDAOImpl();
            case EXPENSE_TYPE:
                return new ExpenseTypeDAOImpl();
            case EMPLOYEE:
                return new EmployeeDAOImpl();
            default:
                return null;
        }
    }
}
