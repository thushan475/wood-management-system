    package lk.ijse.wood_managment.db;

    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;

    public class DBConnection {

        private static DBConnection db;
        private Connection connection;

        private static final String DB_URL = "jdbc:mysql://localhost:3306/wood_management";
        private static final String DB_USER = "root";
        private static final String DB_PASSWORD = "mysql";

        static {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private DBConnection() throws SQLException {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }

        public static synchronized DBConnection getInstance() throws SQLException {
            if (db == null || db.connection == null || db.connection.isClosed()) {
                db = new DBConnection();
            }
            return db;
        }

        public Connection getConnection() throws SQLException {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
            return connection;
        }

    }
