package lk.ijse.wood_managment.util;

public class UserSession {
    private static String userRole;
    private static String username;

    public static void setUserRole(String role) {
        userRole = role;
    }

    public static String getUserRole() {
        return userRole;
    }

    public static void setUsername(String user) {
        username = user;
    }

    public static String getUsername() {
        return username;
    }
}
