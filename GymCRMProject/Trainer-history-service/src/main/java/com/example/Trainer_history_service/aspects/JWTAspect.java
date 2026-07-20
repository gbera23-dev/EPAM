package com.example.Trainer_history_service.aspects;

import com.example.Trainer_history_service.exceptions.UserCannotBeAuthorizedException;
import com.example.Trainer_history_service.services.JWTService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@AllArgsConstructor
@Slf4j
@Component
public class JWTAspect {

    private static final String JWT_TOKEN_PREFIX = "Bearer ";

    @Pointcut("execution(* com.example.Trainer_history_service.consumers.*.*(..))")
    public void consumerLayer() {}

    private final JWTService jwtService;

    @Before("consumerLayer() && args(.., jwtToken, transactionId)")
    private void validateJWTToken(String jwtToken, String transactionId) {
        //strip Bearer from jwt token
        jwtToken = jwtToken.substring(JWT_TOKEN_PREFIX.length());

        //validate jwt token
        boolean tokenIsValid = false;
        try {
           tokenIsValid = jwtService.tokenIsValid(jwtToken);
           log.info("Token was validated, proceeding with message processing...");
        } catch(UserCannotBeAuthorizedException e) {
            log.error("User could not be authorized, jwt validation failed!..");
        }

    }
}
