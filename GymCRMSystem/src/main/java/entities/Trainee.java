package entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class Trainee implements GymEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Trainee_id")
    private long id;

    @Column(name="Date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name="Address")
    private String address;

    @OneToOne
    @JoinColumn(name="User_id")
    private User user;

    @OneToMany(mappedBy="trainee")
    private List<Training> trainings;

    @ManyToMany(mappedBy="trainees")
    private List<Trainer> trainers;

    @Override
    public long getEntityId() {
        return id;
    }
}
