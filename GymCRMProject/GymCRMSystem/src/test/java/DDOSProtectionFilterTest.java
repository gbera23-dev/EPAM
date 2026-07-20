
import app.filters.DDOSProtectionFilter;
import app.services.DDOSProtectionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class DDOSProtectionFilterTest {

    private DDOSProtectionService ddosMock;
    private DDOSProtectionFilter filter;

    @BeforeEach
    public void setup() {
        ddosMock = mock(DDOSProtectionService.class);
        filter = new DDOSProtectionFilter(ddosMock);
    }

    @Test
    public void testDoFilterInternalBlockedUserReturns429() throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/api/user/login");
        req.setRemoteAddr("1.2.3.4");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        doAnswer(invocation -> {
            return null;
        }).when(chain).doFilter(any(), any());
        when(ddosMock.userShouldBeBlocked("1.2.3.4")).thenReturn(true);
        when(ddosMock.userIsBlocked("1.2.3.4")).thenReturn(true);
        filter.doFilter(req, resp, chain);
        assertEquals(429, resp.getStatus());
        String content = resp.getContentAsString();
        assertTrue(content.contains("You are blocked due to numerous failed logging attempts"));
        verify(ddosMock).releaseUsersWithExpiredLocks();
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    public void testDoFilterInternalNotBlockedContinuesFilterChain() throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/api/user/login");
        req.setRemoteAddr("5.6.7.8");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        when(ddosMock.userShouldBeBlocked("5.6.7.8")).thenReturn(false);
        when(ddosMock.userIsBlocked("5.6.7.8")).thenReturn(false);
        filter.doFilter(req, resp, chain);
        verify(chain).doFilter(req, resp);
    }
}