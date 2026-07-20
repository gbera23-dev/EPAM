package com.example.Trainer_history_service;

import com.example.Trainer_history_service.filters.TransactionFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private TransactionFilter transactionFilter;

    @BeforeEach
    void setUp() {
        transactionFilter = new TransactionFilter();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void testDoFilterInternalUsesExistingTransactionId() throws Exception {
        when(request.getHeader("X-Transaction-ID")).thenReturn("abc-123");
        doAnswer(invocation -> {
            assertEquals("abc-123", MDC.get("transactionId"));
            return null;
        }).when(filterChain).doFilter(request, response);

        transactionFilter.doFilter(request, response, filterChain);

        assertNull(MDC.get("transactionId"));
    }

    @Test
    void testDoFilterInternalGeneratesNewTransactionIdWhenHeaderMissing() throws Exception {
        when(request.getHeader("X-Transaction-ID")).thenReturn(null);
        doAnswer(invocation -> {
            assertNotNull(MDC.get("transactionId"));
            return null;
        }).when(filterChain).doFilter(request, response);

        transactionFilter.doFilter(request, response, filterChain);

        assertNull(MDC.get("transactionId"));
    }

    @Test
    void testDoFilterInternalRemovesMdcAfterFilterChainThrowsException() throws Exception {
        when(request.getHeader("X-Transaction-ID")).thenReturn("abc-123");
        doThrow(new ServletException("failure")).when(filterChain).doFilter(request, response);

        assertThrows(ServletException.class,
                () -> transactionFilter.doFilter(request, response, filterChain));

        assertNull(MDC.get("transactionId"));
    }
}
