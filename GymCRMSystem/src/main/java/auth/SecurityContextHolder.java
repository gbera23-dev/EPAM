package auth;

/**
 * Wrapper class around ThreadLocal object that allows us to set, get and clear session of the current user
 */
public class SecurityContextHolder {
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(String username) {
        currentUser.set(username);
    }

    public static String getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}
