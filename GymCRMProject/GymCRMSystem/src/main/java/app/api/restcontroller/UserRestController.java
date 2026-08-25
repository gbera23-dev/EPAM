package app.api.restcontroller;

import app.api.dto.request.LoginRequest;
import app.api.dto.request.PasswordChangeRequest;
import app.services.security.interfaces.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.services.security.interfaces.AuthService;

import java.util.HashMap;
import java.util.Map;

import static app.infrastructure.constants.SecurityConstants.AUTHORIZATION_HEADER;
import static app.infrastructure.constants.SecurityConstants.JWT_TOKEN_PREFIX;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User information management", description = "Operations for user")
public class UserRestController {

    private final AuthService authService;
    private final JWTService jwtService;


    public UserRestController(AuthService authService, JWTService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }


    @Operation(summary = "Login user", description = "Authenticates a user and returns a session token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(type = "object",
                            example = "{\"jwt-token\": \"particular-jwt-token\", \"message\": \"Log in was successful!\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication failed", content = @Content)
    })
    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequest loginRequest,
                                                         HttpServletRequest httpServletRequest) {

        String jwtToken = authService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword(),
                httpServletRequest.getRemoteAddr());

        Map<String, String> output = new HashMap<>();

        output.put("jwt-token", jwtToken);
        output.put("message", "Log in was successful!");

        return ResponseEntity.ok().body(output);
    }

    @Operation(summary = "Logout user", description = "Invalidates the provided JWT token by adding it to the blacklist")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logout successful, token has been blacklisted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Authorization header", content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest httpServletRequest) {
        String authHeader = httpServletRequest.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(JWT_TOKEN_PREFIX)) {
            String token = authHeader.substring(7);
            jwtService.addJWTTokenToBlacklist(token);
        }
        return ResponseEntity.noContent().build();
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

        authService.changeUserProfilePassword(passwordChangeRequest.getUsername(),
                passwordChangeRequest.getOldPassword(),
                passwordChangeRequest.getNewPassword());

        return ResponseEntity.ok().body("Password change was successful!");
    }

}

