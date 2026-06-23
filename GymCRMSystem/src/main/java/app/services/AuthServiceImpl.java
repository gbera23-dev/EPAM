package app.services;

import app.entities.User;
import app.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import app.persistence.UserRepository;
import org.springframework.web.context.request.RequestContextHolder;

@Component
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
    public void changeUserProfilePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username);

        if(user == null)
            throw new UserNotFoundException("User could not be found!");

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
        return jwtService.generateToken(username);
    }

}
