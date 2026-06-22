package app.services;

import app.entities.User;
import app.exceptions.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import app.persistence.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, LocalDateTime> sessions;

    private static final int SESSION_TIMEOUT = 1800;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessions = new ConcurrentHashMap<>();
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean validateUserProfile(String username, String password) {
        User user = userRepository.findByUsername(username);

        if(user == null)
            throw new UserNotFoundException("User could not be found!");

        return user.getPassword().equals(password);
    }

    @Override
    public boolean validateUserSession(String username) {
        return sessions.containsKey(username);
    }


    @Override
    @Transactional
    public void changeUserProfilePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username);

        if(user == null)
            throw new UserNotFoundException("User could not be found!");

        if(!sessions.containsKey(username)) {
            throw new SessionNotFoundException("No sessions exist for user, please login first!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Override
    public void loginUserProfile(String username, String password) {
        User user = userRepository.findByUsername(username);

        if(user == null)
            throw new UserNotFoundException("User could not be found!");

        if(!user.getPassword().equals(password))
            throw new PasswordDoesNotMatchException("Password is incorrect!");

        if(sessions.containsKey(username))
            throw new UserAlreadyLoggedInException("User is already logged in!");

        sessions.put(username, LocalDateTime.now());
    }

    @Override
    public void logoutUserProfile(String username) {

        if(!sessions.containsKey(username)) {
            throw new SessionNotFoundException("No such session exists, cannot logout!");
        }

        sessions.remove(username);
    }

    @Override
    public void cleanUpExpiredSessions() {
        LocalDateTime nowTime = LocalDateTime.now();
        sessions.values().removeIf(sessionTime ->
                ChronoUnit.SECONDS.between(sessionTime, nowTime) >= SESSION_TIMEOUT
        );

    }

}
