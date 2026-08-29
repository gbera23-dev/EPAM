package app.services.security.impl;

import app.aop.annotations.ServiceLayer;
import app.domain.entities.User;
import app.domain.exceptions.PasswordDoesNotMatchException;
import app.domain.exceptions.UserCannotBeAuthorizedException;
import app.domain.exceptions.UserNotFoundException;
import app.services.security.interfaces.AuthService;
import app.services.security.interfaces.DDOSProtectionService;
import app.services.security.interfaces.JWTService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.domain.persistence.UserRepository;

@Service
@ServiceLayer
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final DDOSProtectionService ddosProtectionService;

    public AuthServiceImpl(UserRepository userRepository,
                           AuthenticationManager authenticationManager,
                           JWTService jwtService,
                           PasswordEncoder passwordEncoder,
                           DDOSProtectionService ddosProtectionService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.ddosProtectionService = ddosProtectionService;
    }

    @Override
    @Transactional
    public void changeUserProfilePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException("Could not find user with username!")
                );

        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new PasswordDoesNotMatchException("passwords do not match!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Override
    public String authenticateUser(String username, String password, String ip) {

        UsernamePasswordAuthenticationToken token =  new UsernamePasswordAuthenticationToken(
                username, password
        );

        token.setDetails(ip);

        Authentication authentication = authenticationManager.authenticate(
                token
        );

        if(!authentication.isAuthenticated()) {
            throw new UserCannotBeAuthorizedException("User cannot be authenticated!");
        }

        ddosProtectionService.reloadUserLoginAttempts(ip);
        return jwtService.generateToken(username);
    }

}
