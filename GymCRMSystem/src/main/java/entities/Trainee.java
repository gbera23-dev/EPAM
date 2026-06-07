package entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Trainee implements GymEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Trainee_id")
    private Long id;

    @Column(name="Date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name="Address")
    private String address;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="User_id")
    private User user;

    @OneToMany(mappedBy="trainee", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Training> trainings;

    @ManyToMany(mappedBy="trainees")
    private List<Trainer> trainers;

    @Override
    public long getEntityId() {
        return id;
    }
}
