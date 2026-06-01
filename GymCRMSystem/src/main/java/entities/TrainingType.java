package entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="TrainingType_Id")
    private Long id;

    @Column(name="TrainingType_name", nullable = false)
    private String name;

    @OneToMany(mappedBy= "trainingType")
    private List<Training> trainings;

    @OneToMany(mappedBy="trainingType")
    private List<Trainer> trainers;

}
