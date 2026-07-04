package app.services;

import app.entities.Trainer;
import app.entities.Training;

import java.time.LocalDate;
import java.util.List;

/**
 * CRUD and query operations for {@link entities.Trainer} profiles.
 */
public interface TrainerService {

    /** Creates a new trainer profile, generating user credentials. Returns generated RAW password to user*/
    String createTrainerProfile(Trainer trainer);

    /** Persists changes to an existing trainer profile. */
    void updateTrainerProfile(Trainer trainer);

    /** @throws jakarta.persistence.EntityNotFoundException if not found */
    Trainer selectTrainerProfileById(long trainerId);

    Trainer selectTrainerProfileByUsername(String username);

    /** @throws IllegalStateException if the profile is already active */
    void activateTrainerProfile(long trainerId);

    /** @throws IllegalStateException if the profile is already inactive */
    void deactivateTrainerProfile(long trainerId);

    /** Returns trainings matching all non-null criteria; null params are ignored. */
    List<Training> getTrainingsForTrainer(String username, LocalDate fromDate,
                                          LocalDate toDate, String traineeName);
}