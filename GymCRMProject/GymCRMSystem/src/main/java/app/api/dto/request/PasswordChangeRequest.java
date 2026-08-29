package app.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {

    private String username;

    private String oldPassword;

    private String newPassword;

}
