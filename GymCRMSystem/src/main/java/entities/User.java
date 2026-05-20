package entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class User {
    private long userId;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    @JsonProperty("isActive")
    private boolean active;
}
