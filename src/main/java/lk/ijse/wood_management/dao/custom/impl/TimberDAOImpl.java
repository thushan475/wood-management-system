package lk.ijse.wood_management.dao.custom.impl;

import lk.ijse.wood_management.dao.custom.TimberDAO;
import lk.ijse.wood_management.db.DBConnection;
import lk.ijse.wood_management.entity.Timber;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimberDAOImpl implements TimberDAO {

    public List<Timber> getAll() throws SQLException {
        List<Timber> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM timber")) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public boolean save(Timber t) throws SQLException {
        String checkSql = "SELECT quantity FROM timber WHERE timber_id=? AND width=? AND length=? AND species=?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, t.getTimberId());
            check.setDouble(2, t.getWidth());
            check.setDouble(3, t.getLength());
            check.setString(4, t.getSpecies());
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                String updateSql = "UPDATE timber SET quantity = quantity + ? WHERE timber_id=? AND width=? AND length=? AND species=?";
                try (PreparedStatement upd = conn.prepareStatement(updateSql)) {
                    upd.setDouble(1, t.getQuantity());
                    upd.setInt(2, t.getTimberId());
                    upd.setDouble(3, t.getWidth());
                    upd.setDouble(4, t.getLength());
                    upd.setString(5, t.getSpecies());
                    return upd.executeUpdate() > 0;
                }
            } else {
                String insertSql = "INSERT INTO timber VALUES (?,?,?,?,?,?)";
                try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                    ins.setInt(1, t.getTimberId());
                    ins.setInt(2, t.getCuttingId());
                    ins.setDouble(3, t.getQuantity());
                    ins.setDouble(4, t.getWidth());
                    ins.setDouble(5, t.getLength());
                    ins.setString(6, t.getSpecies());
                    return ins.executeUpdate() > 0;
                }
            }
        }
    }

    public boolean update(Timber t) throws SQLException {
        String sql = "UPDATE timber SET cutting_id=?, quantity=?, width=?, length=?, species=? WHERE timber_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, t.getCuttingId());
            pst.setDouble(2, t.getQuantity());
            pst.setDouble(3, t.getWidth());
            pst.setDouble(4, t.getLength());
            pst.setString(5, t.getSpecies());
            pst.setInt(6, t.getTimberId());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean delete(int timberId) throws SQLException {
        String sql = "DELETE FROM timber WHERE timber_id=?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, timberId);
            return pst.executeUpdate() > 0;
        }
    }

    public List<Timber> search(Integer timberId, Double width, Double length, Double qty, String species) throws SQLException {
        List<Timber> list = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM timber WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (timberId != null) { query.append(" AND timber_id=?"); params.add(timberId); }
        if (width != null)    { query.append(" AND width=?");     params.add(width); }
        if (length != null)   { query.append(" AND length=?");    params.add(length); }
        if (qty != null)      { query.append(" AND quantity=?");  params.add(qty); }
        if (species != null)  { query.append(" AND species LIKE ?"); params.add(species); }

        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement pst = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) pst.setObject(i + 1, params.get(i));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public double getAvailableStock(int timberId, BigDecimal width, BigDecimal length, String species) throws SQLException {
        String sql = "SELECT quantity FROM timber WHERE timber_id=? AND width=? AND length=? AND species=?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, timberId);
            pst.setBigDecimal(2, width.setScale(2, RoundingMode.HALF_UP));
            pst.setBigDecimal(3, length.setScale(2, RoundingMode.HALF_UP));
            pst.setString(4, species);
            ResultSet rs = pst.executeQuery();
            return rs.next() ? rs.getDouble("quantity") : 0;
        }
    }

    public boolean reduceStock(Connection conn, int timberId, BigDecimal width, BigDecimal length, String species, double qty) throws SQLException {
        String sql = "UPDATE timber SET quantity=quantity-? WHERE timber_id=? AND width=? AND length=? AND species=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setDouble(1, qty);
            pst.setInt(2, timberId);
            pst.setBigDecimal(3, width.setScale(2, RoundingMode.HALF_UP));
            pst.setBigDecimal(4, length.setScale(2, RoundingMode.HALF_UP));
            pst.setString(5, species);
            return pst.executeUpdate() > 0;
        }
    }

    public List<Integer> getAllTimberIds() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT DISTINCT timber_id FROM timber WHERE quantity > 0")) {
            while (rs.next()) ids.add(rs.getInt("timber_id"));
        }
        return ids;
    }

    public List<BigDecimal> getAllWidths() throws SQLException {
        List<BigDecimal> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT DISTINCT width FROM timber WHERE quantity > 0")) {
            while (rs.next()) {
                BigDecimal w = rs.getBigDecimal("width").setScale(2, RoundingMode.HALF_UP);
                if (list.stream().noneMatch(x -> x.compareTo(w) == 0)) list.add(w);
            }
        }
        return list;
    }

    public List<BigDecimal> getAllLengths() throws SQLException {
        List<BigDecimal> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT DISTINCT length FROM timber WHERE quantity > 0")) {
            while (rs.next()) {
                BigDecimal l = rs.getBigDecimal("length").setScale(2, RoundingMode.HALF_UP);
                if (list.stream().noneMatch(x -> x.compareTo(l) == 0)) list.add(l);
            }
        }
        return list;
    }

    public List<String> getAllSpecies() throws SQLException {
        List<String> list = new ArrayList<>();
        Connection conn = DBConnection.getInstance().getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT DISTINCT species FROM timber WHERE quantity > 0")) {
            while (rs.next()) list.add(rs.getString("species"));
        }
        return list;
    }

    private Timber mapRow(ResultSet rs) throws SQLException {
        return new Timber(
                rs.getInt("timber_id"), rs.getInt("cutting_id"),
                rs.getDouble("quantity"), rs.getDouble("width"),
                rs.getDouble("length"), rs.getString("species")
        );
    }
}
