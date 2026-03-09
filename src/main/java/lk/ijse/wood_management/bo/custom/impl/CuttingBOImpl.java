package lk.ijse.wood_management.bo.custom.impl;

import lk.ijse.wood_management.bo.custom.CuttingBO;

import lk.ijse.wood_management.dto.CuttingDTO;
import lk.ijse.wood_management.dao.custom.impl.CuttingDAOImpl;

import java.sql.SQLException;
import java.util.List;

public class CuttingBOImpl implements CuttingBO {

    private final CuttingDAOImpl cuttingRepo = new CuttingDAOImpl();

    public List<CuttingDTO> getAll() throws SQLException { return cuttingRepo.getAll(); }
    public boolean addCutting(CuttingDTO dto) throws SQLException { return cuttingRepo.save(dto); }
    public boolean updateCutting(CuttingDTO dto) throws SQLException { return cuttingRepo.update(dto); }
    public boolean deleteCutting(int id) throws SQLException { return cuttingRepo.delete(id); }
    public CuttingDTO findByWoodId(int woodId) throws SQLException { return cuttingRepo.findByWoodId(woodId); }
}
