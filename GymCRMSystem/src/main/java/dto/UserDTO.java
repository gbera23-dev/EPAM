package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class UserDTO {
    private long userId;
    @NotBlank(message="Provided first name must not be Blank!")
    @NotNull(message="Provided first name must not be null!")
    private String firstName;
    @NotBlank(message="Provided last name must not be Blank!")
    @NotNull(message="Provided last name must not be null!")
    private String lastName;
    @NotBlank(message="Provided username must not be Blank!")
    @NotNull(message="Provided username must not be null!")
    private String username;
    @NotBlank(message="Provided password must not be Blank!")
    @NotNull(message="Provided password must not be null!")
    @Size(min = 8, message = "Password must be 8 characters or more!")
    private String password;
    @JsonProperty("isActive")
    private boolean active;
}
