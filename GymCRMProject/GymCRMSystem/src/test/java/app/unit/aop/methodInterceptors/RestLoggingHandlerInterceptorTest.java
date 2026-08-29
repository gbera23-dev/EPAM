package app.unit.aop.methodInterceptors;

import app.aop.methodInterceptors.RestLoggingHandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RestLoggingHandlerInterceptorTest {

    private RestLoggingHandlerInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new RestLoggingHandlerInterceptor();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
    }

    @Test
    void TestPreHandleReturnsTrue() {
        boolean result = interceptor.preHandle(request, response, new Object());
        assertTrue(result);
    }

    @Test
    void TestAfterCompletionLogsSuccess() {
        when(response.getStatus()).thenReturn(200);
        interceptor.afterCompletion(request, response, new Object(), null);
    }

    @Test
    void TestAfterCompletionLogsException() {
        interceptor.afterCompletion(request, response, new Object(), new RuntimeException("failure"));
    }
}
