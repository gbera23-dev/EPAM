package app.auth;

import app.annotations.AuthRequired;
import app.exceptions.UserNotLoggedInException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import app.services.AuthService;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class utilizes AOP to provide Security for the Service layer
 */
@Aspect
@Component
public class AuthAspect {

    private final AuthService authService;


    public AuthAspect(AuthService authService) {
        this.authService = authService;
    }

    @Around("@annotation(authRequired)")
    public Object validateUser(ProceedingJoinPoint pjp, AuthRequired authRequired) throws Throwable {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if(attributes == null) {
            return pjp.proceed();
        }

        HttpServletRequest httpServletRequest = attributes.getRequest();

        String userToken = httpServletRequest.getHeader("user-session");

        if(userToken == null || !authService.validateUserSession(userToken)) {
            throw new UserNotLoggedInException("Please, log in before using this service!");
        }

        return pjp.proceed();
    }


}
