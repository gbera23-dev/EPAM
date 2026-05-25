package entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="User_id")
    private Long id;

    @Column(name="First_name", nullable = false)
    private String firstName;

    @Column(name="Last_name", nullable = false)
    private String lastName;

    @Column(name="Username", nullable = false)
    private String username;

    @Column(name="Password", nullable = false)
    private String password;

    @Column(name="Is_active", nullable = false)
    @JsonProperty("isActive")
    private boolean active;

    @OneToOne(mappedBy = "user", cascade=CascadeType.PERSIST)
    private Trainee trainee;

    @OneToOne(mappedBy = "user", cascade=CascadeType.PERSIST)
    private Trainer trainer;
}
