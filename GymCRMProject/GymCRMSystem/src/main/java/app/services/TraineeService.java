package app.services;

import app.entities.Trainee;
import app.entities.Trainer;
import app.entities.Training;

import java.time.LocalDate;
import java.util.List;

/**
 * CRUD and query operations for {@link entities.Trainee} profiles.
 */
public interface TraineeService {

    /** Creates a new trainee profile, generating user credentials. Returns generated RAW password to user*/
    String createTraineeProfile(Trainee trainee);

    /** @throws jakarta.persistence.EntityNotFoundException if not found */
    Trainee selectTraineeProfileById(long traineeId);

    Trainee selectTraineeProfileByUsername(String username);

    /** Persists changes to an existing trainee profile. */
    void updateTraineeProfile(Trainee trainee);

    /** Replaces the trainee's assigned trainer list with the given usernames. */
    void updateTraineeListOfTrainers(long traineeId, List<String> trainerUsernames);

    void deleteTraineeProfileById(long traineeId);

    void deleteTraineeProfileByUsername(String username);

    /** @throws IllegalStateException if the profile is already active */
    void activateTraineeProfile(long traineeId);

    /** @throws IllegalStateException if the profile is already inactive */
    void deactivateTraineeProfile(long traineeId);

    /** Returns trainings matching all non-null criteria; null params are ignored. */
    List<Training> getTrainingsForTrainee(String username, LocalDate fromDate,
                                          LocalDate toDate, String trainerName,
                                          String trainingTypeName);

    /** @return trainers not yet assigned to the given trainee */
    List<Trainer> getTrainersNotAssignedToTrainee(String username);


    List<Trainer> getTrainersAssignedToTrainee(String username);

    List<Training> getAllTrainingsForTrainee(String username);
}