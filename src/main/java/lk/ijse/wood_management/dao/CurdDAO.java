package lk.ijse.wood_management.dao;
import java.sql.SQLException;
import java.util.List;
public interface CurdDAO<T> extends SuperDAO {
    List<T> getAll() throws SQLException;
    boolean save(T entity) throws SQLException;
    boolean update(T entity) throws SQLException;
    boolean delete(int id) throws SQLException;
    T search(int id) throws SQLException;
}
