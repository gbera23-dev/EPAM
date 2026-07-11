package app.dto.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrainerWorkloadCreationRequest {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
}
