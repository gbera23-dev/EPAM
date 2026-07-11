package app.dto.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerTrainingsRequest {

    @NotBlank
    private String username;

    private LocalDate from;

    private LocalDate to;

    private String traineeName;

}
