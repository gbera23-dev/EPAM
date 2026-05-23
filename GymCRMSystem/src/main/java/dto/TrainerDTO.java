package dto;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class TrainerDTO implements GymDTO {

    private long trainerPk;
    private String specialization;
    private UserDTO user;

    @Override
    public long getEntityId() {
        return this.trainerPk;
    }


}
