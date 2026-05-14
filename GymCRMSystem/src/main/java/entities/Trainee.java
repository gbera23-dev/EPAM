package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@ToString
@Setter
@Getter
public class Trainee implements GymEntity {

    private long TraineePk;
    private LocalDate dateOfBirth;
    private String address;
    private User user;

    @Override
    public long getEntityId() {
        return this.TraineePk;
    }
}
