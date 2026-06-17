package app.dto.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserCredentialsResponse {

    private String username;

    private String password;


}
