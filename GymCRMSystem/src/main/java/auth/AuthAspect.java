package auth;

import annotations.AuthRequired;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import services.AuthService;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class utilizes AOP to provide Security for the Service layer
 */
@Aspect
@Component
public class AuthAspect {

    private final AuthService authService;

    private final AtomicInteger callCounter;

    private static int CHECK_TIME = 5;

    public AuthAspect(AuthService authService) {
        this.authService = authService;
        this.callCounter = new AtomicInteger(0);
    }

    @Around("@annotation(authRequired)")
    public Object validateUser(ProceedingJoinPoint pjp, AuthRequired authRequired) throws Throwable {
        String username = SecurityContextHolder.getCurrentUser();

        if(username == null || !authService.validateUserSession(username))
            throw new IllegalArgumentException("Please, log in before using this service!");

        if (callCounter.incrementAndGet() % CHECK_TIME == 0) {
            authService.cleanUpExpiredSessions();
        }

        return pjp.proceed();
    }


}
