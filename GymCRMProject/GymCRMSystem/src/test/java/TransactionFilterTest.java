import app.filters.TransactionFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionFilterTest {

    @Test
    public void testDoFilterInternalUsesExistingTransactionId() throws ServletException, IOException {
        TransactionFilter filter = new TransactionFilter();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("X-Transaction-ID", "tx-12345");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        doAnswer(invocation -> {
            assertEquals("tx-12345", MDC.get("transactionId"));
            return null;
        }).when(chain).doFilter(any(), any());
        filter.doFilter(req, resp, chain);
        assertNull(MDC.get("transactionId"));
        verify(chain).doFilter(req, resp);
    }

    @Test
    public void testDoFilterInternalGeneratesTransactionIdWhenMissing() throws ServletException, IOException {
        TransactionFilter filter = new TransactionFilter();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        doAnswer(invocation -> {
            String val = MDC.get("transactionId");
            assertNotNull(val);
            assertFalse(val.isBlank());
            return null;
        }).when(chain).doFilter(any(), any());
        filter.doFilter(req, resp, chain);
        assertNull(MDC.get("transactionId"));
        verify(chain).doFilter(req, resp);
    }
}