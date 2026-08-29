package app.infrastructure.mappers;

import app.infrastructure.dto.UserDTO;
import app.domain.entities.User;
import org.springframework.stereotype.Component;

@Component("UserMapper")
public class UserMapper implements Mapper<UserDTO, User> {

    @Override
    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setActive(user.isActive());
        return dto;
    }

    @Override
    public User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(null);
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setActive(dto.isActive());
        return user;
    }
}