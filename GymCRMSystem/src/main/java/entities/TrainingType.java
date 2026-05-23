package entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="TrainingType_Id")
    private long id;

    @Column(name="TrainingType_name", nullable = false)
    private String name;

    @OneToMany(mappedBy= "trainingType")
    private List<Training> trainings;

    @OneToMany(mappedBy="trainingType")
    private List<Trainer> trainers;

}
