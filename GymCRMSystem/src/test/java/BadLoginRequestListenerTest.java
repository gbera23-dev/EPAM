import app.listeners.BadLoginRequestListener;
import app.services.DDOSProtectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;

public class BadLoginRequestListenerTest {

    private DDOSProtectionService ddosService;
    private BadLoginRequestListener listener;

    @BeforeEach
    public void setup() {
        ddosService = mock(DDOSProtectionService.class);
        listener = new BadLoginRequestListener(ddosService);
    }

    @Test
    public void testHandleBadLoginRequestRecordsAttempt() {
        Authentication auth = mock(Authentication.class);
        when(auth.getDetails()).thenReturn("detail-ip");
        AuthenticationFailureBadCredentialsEvent ev = new AuthenticationFailureBadCredentialsEvent(auth, new BadCredentialsException("b"));
        listener.handleBadLoginRequest(ev);
        verify(ddosService).recordUserAttempt("detail-ip");
    }
}