package lk.ijse.wood_management.bo;

import lk.ijse.wood_management.bo.custom.impl.BillBOImpl;
import lk.ijse.wood_management.bo.custom.impl.CustomerBOImpl;
import lk.ijse.wood_management.bo.custom.impl.CuttingBOImpl;
import lk.ijse.wood_management.bo.custom.impl.EmployeeBOImpl;
import lk.ijse.wood_management.bo.custom.impl.ExpenseBOImpl;
import lk.ijse.wood_management.bo.custom.impl.ExpenseTypeBOImpl;
import lk.ijse.wood_management.bo.custom.impl.OrderBOImpl;
import lk.ijse.wood_management.bo.custom.impl.SupplierBOImpl;
import lk.ijse.wood_management.bo.custom.impl.TimberBOImpl;
import lk.ijse.wood_management.bo.custom.impl.WoodBOImpl;

public class BOFactory {
    private static BOFactory instance;
    private BOFactory() {}

    public static BOFactory getInstance() {
        return instance == null ? instance = new BOFactory() : instance;
    }

    public enum BOType {
        CUSTOMER, SUPPLIER, TIMBER, WOOD, CUTTING, ORDER, BILL, EXPENSE, EXPENSE_TYPE, EMPLOYEE
    }

    public SuperBO getBO(BOType type) {
        switch (type) {
            case CUSTOMER:
                return new CustomerBOImpl();
            case SUPPLIER:
                return new SupplierBOImpl();
            case TIMBER:
                return new TimberBOImpl();
            case WOOD:
                return new WoodBOImpl();
            case CUTTING:
                return new CuttingBOImpl();
            case ORDER:
                return new OrderBOImpl();
            case BILL:
                return new BillBOImpl();
            case EXPENSE:
                return new ExpenseBOImpl();
            case EXPENSE_TYPE:
                return new ExpenseTypeBOImpl();
            case EMPLOYEE:
                return new EmployeeBOImpl();
            default:
                return null;
        }
    }
}
