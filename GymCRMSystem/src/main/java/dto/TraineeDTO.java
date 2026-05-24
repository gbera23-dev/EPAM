package dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
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