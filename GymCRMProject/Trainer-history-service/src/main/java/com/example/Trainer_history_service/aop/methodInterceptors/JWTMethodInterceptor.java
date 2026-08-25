package com.example.Trainer_history_service.aop.methodInterceptors;

import com.example.Trainer_history_service.api.exceptions.UserCannotBeAuthorizedException;
import com.example.Trainer_history_service.services.security.interfaces.JWTService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.annotation.Aspect;
import org.jspecify.annotations.Nullable;

import static com.example.Trainer_history_service.infrastructure.constants.SecurityConstants.JWT_TOKEN_PREFIX;

@Aspect
@AllArgsConstructor
@Slf4j
public class JWTMethodInterceptor implements MethodInterceptor {

    private final JWTService jwtService;

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

    @Override
    public @Nullable Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        validateJWTToken((String)args[args.length-2], (String)args[args.length-1]);
        return invocation.proceed();
    }
}
