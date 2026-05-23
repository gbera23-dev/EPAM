package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@ToString
@Setter
@Getter
public class TraineeDTO implements GymDTO {

    private long TraineePk;
    private LocalDate dateOfBirth;
    private String address;
    private UserDTO user;

    @Override
    public long getEntityId() {
        return this.TraineePk;
    }
}