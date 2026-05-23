package entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Training implements GymEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="Training_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="Trainee_id")
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name="Trainer_id")
    private Trainer trainer;

    @Column(name="Training_name", nullable = false)
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="TrainingType_id")
    private TrainingType trainingType;

    @Column(name="Training_data", nullable=false)
    private LocalDate date;

    @Column(name="Duration", nullable=false)
    private int duration;

    @Override
    public long getEntityId() {
        return id;
    }
}
