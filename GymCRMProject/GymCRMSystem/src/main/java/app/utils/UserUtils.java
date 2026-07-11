package app.utils;

import app.entities.User;

import java.util.List;

/**
 * Class combines utilities that are used by the application regarding User entity class
 */
public class UserUtils {

    /**
     * Method Uses string utilities to generate the new username and password for the user
     * @param currentUser The user for which, we are generating new username and password
     * @param users List of users currently in the storage
     */
    public static void generateUserCredentials(User currentUser, List<User> users) {
        String username = StringUtils.generateUsername(currentUser.getFirstName()+"."+
                        currentUser.getLastName(), users.stream().
                        map(User::getUsername).
                        toList());

        String password = StringUtils.generateRandomPassword();

        currentUser.setUsername(username);
        currentUser.setPassword(password);
    }

}
