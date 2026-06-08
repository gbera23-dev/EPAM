package app.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Trainer implements GymEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Trainer_id")
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="Specialization")
    private TrainingType trainingType;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="User_id")
    private User user;

    @OneToMany(mappedBy="trainer")
    private List<Training> trainings;

    @ManyToMany
    @JoinTable(
            name="trainer_trainee",
            joinColumns = @JoinColumn(name="Trainer_id"),
            inverseJoinColumns = @JoinColumn(name="Trainee_id")
    )
    private List<Trainee> trainees;

    @Override
    public long getEntityId() {
        return id;
    }

}
