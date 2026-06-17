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
        doNothing().when(authService).loginUserProfile("john.doe", "pass123");

        mockMvc.perform(get("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john.doe",
                                  "password": "pass123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.session-token").value("john.doe"))
                .andExpect(jsonPath("$.message").value("Log in was successful!"));

        verify(authService).loginUserProfile("john.doe", "pass123");
    }

    @Test
    void testLoginDelegatesCredentialsToService() throws Exception {
        doNothing().when(authService).loginUserProfile("jane.smith", "mypassword");

        mockMvc.perform(get("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "jane.smith",
                                  "password": "mypassword"
                                }
                                """))
                .andExpect(status().isOk());

        verify(authService, times(1)).loginUserProfile("jane.smith", "mypassword");
    }

    @Test
    void testChangePasswordReturns200OnSuccess() throws Exception {
        when(authService.validateUserProfile("john.doe", "oldPass")).thenReturn(true);
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
                .andExpect(content().string("Password change was successful"));

        verify(authService).changeUserProfilePassword("john.doe", "newPass");
    }
}
