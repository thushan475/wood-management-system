package lk.ijse.wood_management.bo.custom.impl;

import lk.ijse.wood_management.bo.custom.SupplierBO;

import lk.ijse.wood_management.dto.SupplierDTO;
import lk.ijse.wood_management.dao.custom.impl.SupplierDAOImpl;

import java.sql.SQLException;
import java.util.List;

public class SupplierBOImpl implements SupplierBO {

    private final SupplierDAOImpl supplierRepo = new SupplierDAOImpl();

    public List<SupplierDTO> getAllSuppliers() throws SQLException {
        return supplierRepo.getAll();
    }

    public boolean addSupplier(SupplierDTO dto) throws SQLException {
        return supplierRepo.save(dto);
    }

    public boolean updateSupplier(SupplierDTO dto) throws SQLException {
        return supplierRepo.update(dto);
    }

    public boolean deleteSupplier(int supplierId) throws SQLException {
        return supplierRepo.delete(supplierId);
    }

    public List<SupplierDTO> searchByName(String name) throws SQLException {
        return supplierRepo.searchByName(name);
    }

    public List<Integer> getAllSupplierIds() throws SQLException {
        return supplierRepo.getAllSupplierIds();
    }
}
