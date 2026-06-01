import annotations.AuthRequired;
import auth.AuthAspect;
import auth.SecurityContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import services.AuthService;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthAspectTest {

    @Mock
    private AuthService authService;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private AuthRequired authRequired;

    @InjectMocks
    private AuthAspect authAspect;

    private MockedStatic<SecurityContextHolder> securityContextHolder;

    @BeforeEach
    void setUp() {
        securityContextHolder = mockStatic(SecurityContextHolder.class);
    }

    @AfterEach
    void tearDown() {
        securityContextHolder.close();
    }

    private void setCallCounter(int value) throws Exception {
        Field field = AuthAspect.class.getDeclaredField("callCounter");
        field.setAccessible(true);
        ((AtomicInteger) field.get(authAspect)).set(value);
    }

    @Test
    void testValidateUserProceedsWhenSessionIsValid() throws Throwable {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn("john.doe");
        when(authService.validateUserSession("john.doe")).thenReturn(true);
        when(pjp.proceed()).thenReturn(null);

        authAspect.validateUser(pjp, authRequired);

        verify(pjp).proceed();
    }

    @Test
    void testValidateUserReturnsResultFromProceed() throws Throwable {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn("john.doe");
        when(authService.validateUserSession("john.doe")).thenReturn(true);
        when(pjp.proceed()).thenReturn("expectedResult");

        Object result = authAspect.validateUser(pjp, authRequired);

        assertEquals("expectedResult", result);
    }

    @Test
    void testValidateUserThrowsWhenNoCurrentUser() {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> authAspect.validateUser(pjp, authRequired));

        verifyNoInteractions(pjp);
    }

    @Test
    void testValidateUserThrowsWhenSessionInvalid() {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn("john.doe");
        when(authService.validateUserSession("john.doe")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authAspect.validateUser(pjp, authRequired));

        verifyNoInteractions(pjp);
    }

    @Test
    void testValidateUserTriggersCleanUpOnEveryFifthCall() throws Throwable {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn("john.doe");
        when(authService.validateUserSession("john.doe")).thenReturn(true);
        when(pjp.proceed()).thenReturn(null);
        setCallCounter(4);

        authAspect.validateUser(pjp, authRequired);

        verify(authService).cleanUpExpiredSessions();
    }

    @Test
    void testValidateUserDoesNotTriggerCleanUpBeforeFifthCall() throws Throwable {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn("john.doe");
        when(authService.validateUserSession("john.doe")).thenReturn(true);
        when(pjp.proceed()).thenReturn(null);
        setCallCounter(3);

        authAspect.validateUser(pjp, authRequired);

        verify(authService, never()).cleanUpExpiredSessions();
    }

    @Test
    void testValidateUserIncrementsCallCounterOnEachCall() throws Throwable {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn("john.doe");
        when(authService.validateUserSession("john.doe")).thenReturn(true);
        when(pjp.proceed()).thenReturn(null);

        authAspect.validateUser(pjp, authRequired);
        authAspect.validateUser(pjp, authRequired);
        authAspect.validateUser(pjp, authRequired);

        Field field = AuthAspect.class.getDeclaredField("callCounter");
        field.setAccessible(true);
        assertEquals(3, ((AtomicInteger) field.get(authAspect)).get());
    }
}