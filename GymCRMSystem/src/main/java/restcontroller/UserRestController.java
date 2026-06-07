package restcontroller;

import annotations.AuthRequired;
import dto.api.request.LoginRequest;
import dto.api.request.PasswordChangeRequest;
import exceptions.PasswordDoesNotMatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/user")
public class UserRestController {

    private final AuthService authService;


    public UserRestController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequest loginRequest) {

        authService.loginUserProfile(loginRequest.getUsername(), loginRequest.getPassword());

        Map<String, String> output = new HashMap<>();

        output.put("session-token", loginRequest.getUsername());
        output.put("message", "Log in was successful!");

        return ResponseEntity.ok().body(output);
    }

    @AuthRequired
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
