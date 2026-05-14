package entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Setter
@Getter
public class Trainer implements GymEntity {

    private long trainerPk;
    private String specialization;
    private User user;

    @Override
    public long getEntityId() {
        return this.trainerPk;
    }

}
