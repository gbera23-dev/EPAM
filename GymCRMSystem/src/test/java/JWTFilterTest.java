import app.filters.JWTFilter;
import app.services.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JWTFilterTest {

    private JWTService jwtService;
    private UserDetailsService userDetailsService;
    private JWTFilter filter;

    @BeforeEach
    public void setup() {
        jwtService = mock(JWTService.class);
        userDetailsService = mock(UserDetailsService.class);
        filter = new JWTFilter(jwtService, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    public void teardown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testDoFilterInternalSetsAuthenticationWhenTokenValidAndNotBlacklisted() throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer sometoken");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        when(jwtService.extractUsernameFromToken("sometoken")).thenReturn("alice");
        when(jwtService.tokenIsBlacklisted("sometoken")).thenReturn(false);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(java.util.List.of());
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(userDetails);
        when(jwtService.tokenIsValid("sometoken", userDetails)).thenReturn(true);
        doAnswer(invocation -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertNotNull(auth);
            assertEquals(userDetails, auth.getPrincipal());
            return null;
        }).when(chain).doFilter(any(), any());
        filter.doFilter(req, resp, chain);
        verify(jwtService).extractUsernameFromToken("sometoken");
        verify(jwtService).tokenIsBlacklisted("sometoken");
        verify(userDetailsService).loadUserByUsername("alice");
        verify(jwtService).tokenIsValid("sometoken", userDetails);
        verify(chain).doFilter(req, resp);
    }

    @Test
    public void testDoFilterInternalDoesNotAuthenticateWhenTokenBlacklisted() throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer blacktoken");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        when(jwtService.extractUsernameFromToken("blacktoken")).thenReturn("bob");
        when(jwtService.tokenIsBlacklisted("blacktoken")).thenReturn(true);
        doAnswer(invocation -> {
            assertNull(req.getAttribute("isAuthenticated"));
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            return null;
        }).when(chain).doFilter(any(), any());
        filter.doFilter(req, resp, chain);
        verify(jwtService).extractUsernameFromToken("blacktoken");
        verify(jwtService).tokenIsBlacklisted("blacktoken");
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(chain).doFilter(req, resp);
    }

    @Test
    public void testDoFilterInternalSkipsWhenAuthenticationAlreadyPresent() throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer sometoken2");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        Authentication preAuth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(preAuth);
        when(jwtService.extractUsernameFromToken("sometoken2")).thenReturn("charlie");
        when(jwtService.tokenIsBlacklisted("sometoken2")).thenReturn(false);
        doAnswer(invocation -> {
            assertNull(req.getAttribute("isAuthenticated"));
            assertEquals(preAuth, SecurityContextHolder.getContext().getAuthentication());
            return null;
        }).when(chain).doFilter(any(), any());
        filter.doFilter(req, resp, chain);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(chain).doFilter(req, resp);
    }

    @Test
    public void testDoFilterInternalSkipsWhenHeaderMissing() throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        doAnswer(invocation -> {
            assertNull(req.getAttribute("isAuthenticated"));
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            return null;
        }).when(chain).doFilter(any(), any());
        filter.doFilter(req, resp, chain);
        verify(jwtService, never()).extractUsernameFromToken(anyString());
        verify(chain).doFilter(req, resp);
    }
}