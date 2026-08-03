package com.example.Trainer_history_service.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="trainer_workloads")
@CompoundIndex(name = "idx_trainer_first_last_name", def = "{'firstName': 1, 'lastName': 1}")
public class TrainerWorkload {
    @MongoId
    private String id;
    @Indexed
    @Field(name="username")
    private String username;
    @Field(name="first_name")
    private String firstName;
    @Field(name="last_name")
    private String lastName;
    @Field(name="status")
    private boolean active;
    @DocumentReference
    private List<MonthlySummary> monthlySummaryList;
}
