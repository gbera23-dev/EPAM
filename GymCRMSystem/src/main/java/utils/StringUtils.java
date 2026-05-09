package utils;

import java.util.List;
import java.util.Random;

public class StringUtils {

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
