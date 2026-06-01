package dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotBlank(message="Specialization cannot be Blank!")
    @NotNull(message="Provided name cannot not be null!")
    private String name;

    @NotNull(message="Provided trainingTypeDTO must not be null!")
    @Valid
    private TrainingTypeDTO trainingTypeDto;

    @NotNull(message="Provided date must not be null!")
    private LocalDate date;

    @Positive(message="Duration must be a positive number!")
    private int duration;

    @Override
    public long getEntityId() {
        return this.trainingPk;
    }
}
