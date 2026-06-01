package services;

import entities.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import persistence.UserRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final Map<String, LocalDateTime> sessions;

    private static final int SESSION_TIMEOUT = 1800;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.sessions = new ConcurrentHashMap<>();
    }

    @Override
    public boolean validateUserProfile(String username, String password) {
        User user = userRepository.findByUsername(username);

        if(user == null)
            throw new EntityNotFoundException("User could not be found!");

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
            throw new EntityNotFoundException("User could not be found!");

        if(!sessions.containsKey(username)) {
            throw new EntityNotFoundException("No sessions exist for user, please login first!");
        }

        user.setPassword(newPassword);
    }

    @Override
    public void loginUserProfile(String username, String password) {
        User user = userRepository.findByUsername(username);

        if(user == null)
            throw new EntityNotFoundException("User could not be found!");

        if(!user.getPassword().equals(password))
            throw new IllegalArgumentException("Password is incorrect!");

        if(sessions.containsKey(username))
            throw new IllegalArgumentException("User is already logged in!");

        sessions.put(username, LocalDateTime.now());
    }

    @Override
    public void logoutUserProfile(String username) {

        if(!sessions.containsKey(username)) {
            throw new EntityNotFoundException("No such session exists, cannot logout!");
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
