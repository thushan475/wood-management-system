package lk.ijse.wood_management.bo.custom.impl;

import lk.ijse.wood_management.bo.custom.WoodBO;

import lk.ijse.wood_management.dto.WoodDTO;
import lk.ijse.wood_management.entity.Wood;
import lk.ijse.wood_management.dao.custom.impl.ExpenseDAOImpl;
import lk.ijse.wood_management.dao.custom.impl.WoodDAOImpl;
import lk.ijse.wood_management.db.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class WoodBOImpl implements WoodBO {

    private final WoodDAOImpl woodRepo = new WoodDAOImpl();
    private final ExpenseDAOImpl expenseRepo = new ExpenseDAOImpl();

    public List<Wood> getAllWood() throws SQLException {
        return woodRepo.getAll();
    }


    public void addWoodWithExpense(WoodDTO dto) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                int woodId = woodRepo.save(dto);
                if (woodId < 0) throw new SQLException("Wood insert failed");

                double amount = (dto.getLength() * dto.getWidth() * dto.getLength()) / 2304 * dto.getUnitPrice();
                LocalDate purchaseDate = dto.getPurchaseDate().toLocalDate();

                expenseRepo.saveWithConnection(conn, woodId, "Buy Wood", "Wood purchase",
                        dto.getSpecies(), dto.getLength(), dto.getWidth(), dto.getUnitPrice(), amount, purchaseDate);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean updateWood(WoodDTO dto) throws SQLException {
        return woodRepo.update(dto);
    }

    public boolean deleteWood(int woodId) throws SQLException {
        return woodRepo.delete(woodId);
    }

    public List<Wood> searchWood(String species, Integer supplierId) throws SQLException {
        return woodRepo.search(species, supplierId);
    }
}
