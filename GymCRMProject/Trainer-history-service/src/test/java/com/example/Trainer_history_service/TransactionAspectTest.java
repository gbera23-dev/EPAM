package com.example.Trainer_history_service;

import com.example.Trainer_history_service.aspects.TransactionAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionAspectTest {

    @Mock private ProceedingJoinPoint pjp;

    private TransactionAspect transactionAspect;
    private Method passTransactionIdToLogs;

    @BeforeEach
    void setUp() throws Exception {
        transactionAspect = new TransactionAspect();
        passTransactionIdToLogs = TransactionAspect.class.getDeclaredMethod(
                "passTransactionIdToLogs", ProceedingJoinPoint.class, String.class);
        passTransactionIdToLogs.setAccessible(true);
        MDC.clear();
    }

    @Test
    void testPassTransactionIdToLogsMakesTransactionIdAvailableDuringProceed() throws Throwable {
        AtomicReference<String> mdcDuringProceed = new AtomicReference<>();
        when(pjp.proceed()).thenAnswer(invocation -> {
            mdcDuringProceed.set(MDC.get("transactionId"));
            return "result";
        });

        Object result = passTransactionIdToLogs.invoke(transactionAspect, pjp, "txn-99");

        assertEquals("result", result);
        assertEquals("txn-99", mdcDuringProceed.get());
    }

    @Test
    void testPassTransactionIdToLogsRemovesTransactionIdAfterProceed() throws Throwable {
        when(pjp.proceed()).thenReturn("result");

        passTransactionIdToLogs.invoke(transactionAspect, pjp, "txn-100");

        assertNull(MDC.get("transactionId"));
    }

    @Test
    void testPassTransactionIdToLogsRemovesTransactionIdEvenWhenProceedThrows() throws Throwable {
        when(pjp.proceed()).thenThrow(new RuntimeException("boom"));

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class,
                () -> passTransactionIdToLogs.invoke(transactionAspect, pjp, "txn-101"));

        assertInstanceOf(RuntimeException.class, thrown.getCause());
        assertNull(MDC.get("transactionId"));
    }
}
