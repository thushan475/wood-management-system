package lk.ijse.wood_management.bo.custom.impl;

import lk.ijse.wood_management.bo.custom.TimberBO;

import lk.ijse.wood_management.entity.Timber;
import lk.ijse.wood_management.dao.custom.impl.TimberDAOImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TimberBOImpl implements TimberBO {

    private final TimberDAOImpl timberRepo = new TimberDAOImpl();

    public List<Timber> getAllTimber() throws SQLException {
        return timberRepo.getAll();
    }

    public boolean addTimber(Timber t) throws SQLException {
        return timberRepo.save(t);
    }

    public boolean updateTimber(Timber t) throws SQLException {
        return timberRepo.update(t);
    }

    public boolean deleteTimber(int timberId) throws SQLException {
        return timberRepo.delete(timberId);
    }

    public List<Timber> searchTimber(Integer timberId, Double width, Double length, Double qty, String species) throws SQLException {
        return timberRepo.search(timberId, width, length, qty, species);
    }

    public double getAvailableStock(int timberId, BigDecimal width, BigDecimal length, String species) throws SQLException {
        return timberRepo.getAvailableStock(timberId, width, length, species);
    }

    public boolean reduceStock(Connection conn, int timberId, BigDecimal width, BigDecimal length, String species, double qty) throws SQLException {
        return timberRepo.reduceStock(conn, timberId, width, length, species, qty);
    }

    public List<Integer> getAllTimberIds() throws SQLException { return timberRepo.getAllTimberIds(); }
    public List<BigDecimal> getAllWidths() throws SQLException { return timberRepo.getAllWidths(); }
    public List<BigDecimal> getAllLengths() throws SQLException { return timberRepo.getAllLengths(); }
    public List<String> getAllSpecies() throws SQLException { return timberRepo.getAllSpecies(); }
}
