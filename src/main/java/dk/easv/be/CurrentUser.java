package dk.easv.be;

public class CurrentUser {
    private static Users user;

    public static void setUser(Users user) {
        CurrentUser.user = user;
    }

    public static Users getUser() {
        return user;
    }

    public static void clear() {
        user = null;
    }

    public static boolean isLoggedIn() {
        return user != null;
    }

    public static boolean isAdmin() {
        return user != null && "Admin".equalsIgnoreCase(user.getRole());
    }

    public static boolean isCoordinator() {
        return user != null && "EventCoordinator".equalsIgnoreCase(user.getRole());
    }
}