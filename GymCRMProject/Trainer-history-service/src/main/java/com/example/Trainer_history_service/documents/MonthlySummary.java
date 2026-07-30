package com.example.Trainer_history_service.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="monthly_summaries")
public class MonthlySummary {
    @MongoId
    private String id;
    @Field(name="date")
    private LocalDate date;
    @Field(name="duration")
    private Integer duration;
    @DBRef
    private TrainerWorkload trainerWorkload;
}