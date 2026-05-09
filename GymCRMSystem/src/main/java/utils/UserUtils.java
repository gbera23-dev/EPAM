package utils;

import entities.User;

import java.util.List;

public class UserUtils {


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
