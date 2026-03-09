package lk.ijse.wood_management.dao;

import lk.ijse.wood_management.db.DBConnection;
import java.sql.*;

public class CRUDUtil {

    @SuppressWarnings("unchecked")
    public static <T> T execute(String sql, Object... params) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < params.length; i++) {
            pst.setObject(i + 1, params[i]);
        }
        if (sql.trim().toUpperCase().startsWith("SELECT")) {
            return (T) pst.executeQuery();
        } else {
            return (T) Boolean.valueOf(pst.executeUpdate() > 0);
        }
    }

    public static ResultSet query(String sql, Object... params) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement pst = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            pst.setObject(i + 1, params[i]);
        }
        return pst.executeQuery();
    }
}
