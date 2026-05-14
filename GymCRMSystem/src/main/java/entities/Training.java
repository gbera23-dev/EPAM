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
public class Training implements GymEntity {

    private long trainingPk;
    private long traineeId;
    private long trainerId;
    private String name;
    private TrainingType trainingType;
    private LocalDate date;
    private int duration;

    @Override
    public long getEntityId() {
        return this.trainingPk;
    }
}
