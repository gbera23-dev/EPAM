import dto.UserDTO;
import entities.User;
import mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void testToDtoMapsUserId() {
        User user = buildUser(10L, "Alice", "Smith", "alice.smith", "pass123", true);

        UserDTO dto = userMapper.toDTO(user);

        assertEquals(10L, dto.getUserId());
    }

    @Test
    void testToDtoMapsFirstName() {
        User user = buildUser(1L, "Alice", "Smith", "alice.smith", "pass123", true);

        UserDTO dto = userMapper.toDTO(user);

        assertEquals("Alice", dto.getFirstName());
    }

    @Test
    void testToDtoMapsLastName() {
        User user = buildUser(1L, "Alice", "Smith", "alice.smith", "pass123", true);

        UserDTO dto = userMapper.toDTO(user);

        assertEquals("Smith", dto.getLastName());
    }

    @Test
    void testToDtoMapsUsername() {
        User user = buildUser(1L, "Alice", "Smith", "alice.smith", "pass123", true);

        UserDTO dto = userMapper.toDTO(user);

        assertEquals("alice.smith", dto.getUsername());
    }

    @Test
    void testToDtoMapsPassword() {
        User user = buildUser(1L, "Alice", "Smith", "alice.smith", "pass123", true);

        UserDTO dto = userMapper.toDTO(user);

        assertEquals("pass123", dto.getPassword());
    }

    @Test
    void testToDtoMapsActiveTrue() {
        User user = buildUser(1L, "Alice", "Smith", "alice.smith", "pass123", true);

        UserDTO dto = userMapper.toDTO(user);

        assertTrue(dto.isActive());
    }

    @Test
    void testToDtoMapsActiveFalse() {
        User user = buildUser(1L, "Alice", "Smith", "alice.smith", "pass123", false);

        UserDTO dto = userMapper.toDTO(user);

        assertFalse(dto.isActive());
    }

    @Test
    void testToEntitySetsIdToNull() {
        UserDTO dto = buildUserDTO(5L, "Bob", "Jones", "bob.jones", "secret", true);

        User user = userMapper.toEntity(dto);

        assertNull(user.getId());
    }

    @Test
    void testToEntityMapsFirstName() {
        UserDTO dto = buildUserDTO(5L, "Bob", "Jones", "bob.jones", "secret", true);

        User user = userMapper.toEntity(dto);

        assertEquals("Bob", user.getFirstName());
    }

    @Test
    void testToEntityMapsLastName() {
        UserDTO dto = buildUserDTO(5L, "Bob", "Jones", "bob.jones", "secret", true);

        User user = userMapper.toEntity(dto);

        assertEquals("Jones", user.getLastName());
    }

    @Test
    void testToEntityMapsUsername() {
        UserDTO dto = buildUserDTO(5L, "Bob", "Jones", "bob.jones", "secret", true);

        User user = userMapper.toEntity(dto);

        assertEquals("bob.jones", user.getUsername());
    }

    @Test
    void testToEntityMapsPassword() {
        UserDTO dto = buildUserDTO(5L, "Bob", "Jones", "bob.jones", "secret", true);

        User user = userMapper.toEntity(dto);

        assertEquals("secret", user.getPassword());
    }

    @Test
    void testToEntityMapsActiveTrue() {
        UserDTO dto = buildUserDTO(5L, "Bob", "Jones", "bob.jones", "secret", true);

        User user = userMapper.toEntity(dto);

        assertTrue(user.isActive());
    }

    @Test
    void testToEntityMapsActiveFalse() {
        UserDTO dto = buildUserDTO(5L, "Bob", "Jones", "bob.jones", "secret", false);

        User user = userMapper.toEntity(dto);

        assertFalse(user.isActive());
    }

    private User buildUser(Long id, String firstName, String lastName,
                           String username, String password, boolean active) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setActive(active);
        return user;
    }

    private UserDTO buildUserDTO(Long id, String firstName, String lastName,
                                  String username, String password, boolean active) {
        UserDTO dto = new UserDTO();
        dto.setUserId(id);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setActive(active);
        return dto;
    }
}
