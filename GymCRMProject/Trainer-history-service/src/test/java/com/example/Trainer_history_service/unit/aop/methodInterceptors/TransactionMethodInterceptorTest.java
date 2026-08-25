package com.example.Trainer_history_service.unit.aop.methodInterceptors;

import com.example.Trainer_history_service.aop.methodInterceptors.TransactionMethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static com.example.Trainer_history_service.infrastructure.constants.TransactionConstants.MDC_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionMethodInterceptorTest {

    @Test
    void TestInvokeReturnsResultAndClearsMdc() throws Throwable {
        MethodInvocation invocation = mock(MethodInvocation.class);
        Object[] args = {"payload", "transaction-1"};
        when(invocation.getArguments()).thenReturn(args);
        when(invocation.proceed()).thenReturn("proceeded");

        TransactionMethodInterceptor interceptor = new TransactionMethodInterceptor();
        Object result = interceptor.invoke(invocation);

        assertEquals("proceeded", result);
        assertNull(MDC.get(MDC_KEY));
    }

    @Test
    void TestInvokeClearsMdcOnException() throws Throwable {
        MethodInvocation invocation = mock(MethodInvocation.class);
        Object[] args = {"payload", "transaction-1"};
        when(invocation.getArguments()).thenReturn(args);
        when(invocation.proceed()).thenThrow(new RuntimeException("boom"));

        TransactionMethodInterceptor interceptor = new TransactionMethodInterceptor();

        assertThrows(RuntimeException.class, () -> interceptor.invoke(invocation));
        assertNull(MDC.get(MDC_KEY));
    }
}
