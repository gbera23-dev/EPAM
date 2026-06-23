import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import app.restcontroller.UserRestController;
import app.services.AuthService;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;
    @InjectMocks
    private UserRestController userRestController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userRestController).build();
    }

    @Test
    void testLoginReturns200WithSessionToken() throws Exception {
        when(authService.authenticateUser(any(String.class), any(String.class), any(String.class))).thenReturn("jwt-token");

        mockMvc.perform(get("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john.doe",
                                  "password": "pass123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt-token").value("jwt-token"))
                .andExpect(jsonPath("$.message").value("Log in was successful!"));
    }

    @Test
    void testLoginDelegatesCredentialsToService() throws Exception {
        when(authService.authenticateUser(any(String.class), any(String.class), any(String.class))).thenReturn("jwt-token");

        mockMvc.perform(get("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "jane.smith",
                                  "password": "mypassword"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void testChangePasswordReturns200OnSuccess() throws Exception {
        doNothing().when(authService).changeUserProfilePassword("john.doe", "newPass");

        mockMvc.perform(put("/api/user/password-change")
                        .header("user-session", "john.doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john.doe",
                                  "oldPassword": "oldPass",
                                  "newPassword": "newPass"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Password change was successful!"));

    }
}