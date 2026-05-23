package dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class TrainingDTO implements GymDTO {

    private long trainingPk;
    private long traineeId;
    private long trainerId;
    private String name;
    private TrainingTypeDTO trainingTypeDto;
    private LocalDate date;
    private int duration;

    @Override
    public long getEntityId() {
        return this.trainingPk;
    }
}
