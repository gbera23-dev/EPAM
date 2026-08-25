package com.example.Trainer_history_service.unit.aop.methodInterceptors;

import com.example.Trainer_history_service.api.exceptions.UserCannotBeAuthorizedException;
import com.example.Trainer_history_service.aop.methodInterceptors.JWTMethodInterceptor;
import com.example.Trainer_history_service.services.security.interfaces.JWTService;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;

import static com.example.Trainer_history_service.infrastructure.constants.SecurityConstants.JWT_TOKEN_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JWTMethodInterceptorTest {

    @Test
    void TestInvokeProceedsWhenTokenValid() throws Throwable {
        JWTService jwtService = mock(JWTService.class);
        MethodInvocation invocation = mock(MethodInvocation.class);
        Object[] args = {JWT_TOKEN_PREFIX + "validToken", "transaction-1"};
        when(invocation.getArguments()).thenReturn(args);
        when(jwtService.tokenIsValid("validToken")).thenReturn(true);
        when(invocation.proceed()).thenReturn("proceeded");

        JWTMethodInterceptor interceptor = new JWTMethodInterceptor(jwtService);
        Object result = interceptor.invoke(invocation);

        assertEquals("proceeded", result);
        verify(invocation).proceed();
    }

    @Test
    void TestInvokeProceedsWhenTokenValidationThrows() throws Throwable {
        JWTService jwtService = mock(JWTService.class);
        MethodInvocation invocation = mock(MethodInvocation.class);
        Object[] args = {JWT_TOKEN_PREFIX + "invalidToken", "transaction-1"};
        when(invocation.getArguments()).thenReturn(args);
        when(jwtService.tokenIsValid("invalidToken")).thenThrow(new UserCannotBeAuthorizedException("bad token"));
        when(invocation.proceed()).thenReturn("proceeded");

        JWTMethodInterceptor interceptor = new JWTMethodInterceptor(jwtService);
        Object result = interceptor.invoke(invocation);

        assertEquals("proceeded", result);
        verify(invocation).proceed();
    }
}
