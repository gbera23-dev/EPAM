import app.entities.User;
import app.persistence.UserRepository;
import app.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDetailsServiceImplTest {

    private UserRepository userRepository;
    private UserDetailsServiceImpl uds;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        uds = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    public void testLoadUserByUsernameReturnsUserDetails() {
        User u = new User();
        u.setUsername("u1");
        u.setPassword("p1");
        when(userRepository.findByUsername("u1")).thenReturn(u);
        UserDetails details = uds.loadUserByUsername("u1");
        assertEquals("u1", details.getUsername());
        assertEquals("p1", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    public void testLoadUserByUsernameThrowsWhenNotFound() {
        when(userRepository.findByUsername("nope")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> uds.loadUserByUsername("nope"));
    }
}