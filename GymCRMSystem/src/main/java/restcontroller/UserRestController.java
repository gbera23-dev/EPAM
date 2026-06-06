package restcontroller;

import dto.api.request.LoginRequest;
import dto.api.request.PasswordChangeRequest;
import exceptions.PasswordDoesNotMatchException;
import jakarta.transaction.UserTransaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.AuthService;

@RestController
@RequestMapping("api/user")
public class UserRestController {

    private final AuthService authService;


    public UserRestController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest) {

        authService.loginUserProfile(loginRequest.getUsername(), loginRequest.getPassword());

        return ResponseEntity.ok().body("User was successfully logged in!");
    }


    @PutMapping("/password-change")
    public ResponseEntity<String> changeUserPassword
            (@RequestBody PasswordChangeRequest passwordChangeRequest) {

        if(!authService.validateUserProfile(passwordChangeRequest.getUsername(), passwordChangeRequest.getOldPassword())) {
            throw new PasswordDoesNotMatchException("Given password for the user is not correct, please, provide correct" +
                    "password!");
        }

        authService.changeUserProfilePassword(passwordChangeRequest.getUsername(), passwordChangeRequest.getNewPassword());

        return ResponseEntity.ok().body("Password change was successful");
    }

}
