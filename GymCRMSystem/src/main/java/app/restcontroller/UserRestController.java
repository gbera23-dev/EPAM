package app.restcontroller;

import app.dto.api.request.LoginRequest;
import app.dto.api.request.PasswordChangeRequest;
import app.exceptions.PasswordDoesNotMatchException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.services.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User information management", description = "Operations for user")
public class UserRestController {

    private final AuthService authService;


    public UserRestController(AuthService authService) {
        this.authService = authService;
    }


    @Operation(summary = "Login user", description = "Authenticates a user and returns a session token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(type = "object",
                            example = "{\"session-token\": \"john.doe\", \"message\": \"Log in was successful!\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication failed", content = @Content)
    })
    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequest loginRequest) {

        authService.loginUserProfile(loginRequest.getUsername(), loginRequest.getPassword());

        Map<String, String> output = new HashMap<>();

        output.put("session-token", loginRequest.getUsername());
        output.put("message", "Log in was successful!");

        return ResponseEntity.ok().body(output);
    }

    @Operation(summary = "Change user password", description = "Validates the old password and replaces it with a new one")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Old password does not match", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    
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
