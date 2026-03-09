package lk.ijse.wood_management.bo.custom.impl;

import lk.ijse.wood_management.bo.custom.BillBO;

import lk.ijse.wood_management.entity.Bill;
import lk.ijse.wood_management.dao.custom.impl.BillDAOImpl;

import java.sql.SQLException;
import java.util.List;

public class BillBOImpl implements BillBO {

    private final BillDAOImpl billRepo = new BillDAOImpl();

    public List<Bill> getAllBills() throws SQLException { return billRepo.getAll(); }
    public List<Bill> findById(int billId) throws SQLException { return billRepo.findById(billId); }
    public List<Integer> getAllOrderIds() throws SQLException { return billRepo.getAllOrderIds(); }

    public boolean addBill(Bill bill, Integer customBillId) throws SQLException {
        if (customBillId != null && billRepo.existsById(customBillId)) {
            throw new IllegalStateException("Bill ID already exists! Leave empty for auto-generation.");
        }
        return billRepo.save(bill, customBillId);
    }

    public boolean updateBill(Bill bill) throws SQLException { return billRepo.update(bill); }
    public boolean deleteBill(int billId) throws SQLException { return billRepo.delete(billId); }
}
