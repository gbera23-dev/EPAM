package app.utils;

import java.util.List;
import java.util.Random;

/**
 * Class combines Utilities that are used by the application regarding Strings
 */
public class StringUtils {
    /**
     * Generates a random password using allowed Characters and password length is equal to 10
     * @return Generated random password
     */
    public static String generateRandomPassword() {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(allowedChars.length());
            sb.append(allowedChars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Generates the username needed to maintain uniqueness of the user credentials
     * @param currentUsername Username from which we are generating new username
     * @param usernames List of all usernames currently inside Storage
     * @return Generated username
     */
    public static String generateUsername(String currentUsername, List<String> usernames) {
        if(currentUsername == null || currentUsername.isEmpty() || currentUsername.replaceAll("[0-9]", "")
                .length() < currentUsername.length()) {
            throw new IllegalArgumentException();
        }

        long count = usernames.stream().filter(t -> t.replaceAll("[0-9]", "").
                equals(currentUsername))
                .map(str -> str.replaceAll("[^0-9]", ""))
                .mapToLong(str -> !str.isEmpty() ? Long.parseLong(str) : 0L)
                .max()
                .orElse(-1);

        return count != -1 ? currentUsername + (count + 1) : currentUsername;
    }

}
