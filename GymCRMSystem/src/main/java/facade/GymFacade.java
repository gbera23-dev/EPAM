package facade;

import annotations.AuthRequired;
import auth.SecurityContextHolder;
import dto.internal.TraineeDTO;
import dto.internal.TrainerDTO;
import dto.internal.TrainingDTO;
import entities.Trainer;
import entities.Training;
import jakarta.validation.Valid;
import mappers.internal.GymMapper;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import services.AuthService;
import services.TraineeService;
import services.TrainerService;
import services.TrainingService;

import java.time.LocalDate;
import java.util.List;

@Component
@Validated
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final AuthService authService;
    private final GymMapper mapper;

    public GymFacade(TraineeService traineeService, TrainerService trainerService,
                     TrainingService trainingService,
                     AuthService authService,
                     GymMapper mapper) {
        this.trainingService = trainingService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.authService = authService;
        this.mapper = mapper;
    }


    public void loginUser(String username, String password) {
        String currentUser = SecurityContextHolder.getCurrentUser();

        if(currentUser != null) {
            throw new IllegalStateException("You are already logged in!");
        }

        if(!authService.validateUserProfile(username, password))
            throw new IllegalArgumentException("Username or password is incorrect!");

        SecurityContextHolder.setCurrentUser(username);
        authService.loginUserProfile(username, password);
    }

    @AuthRequired
    public void logoutUser() {
        String currentUser = SecurityContextHolder.getCurrentUser();

        if(currentUser == null)
            throw new IllegalStateException("Please, log in first!");

        authService.logoutUserProfile(currentUser);
        SecurityContextHolder.clear();
    }

    @AuthRequired
    public void changeUserPassword(String newPassword) {
        String currentUser = SecurityContextHolder.getCurrentUser();

        if(currentUser == null)
            throw new IllegalStateException("Please, log in first to change the password!");

        authService.changeUserProfilePassword(currentUser, newPassword);
    }

    public void createTrainee(@Valid TraineeDTO trainee) {
        traineeService.createTraineeProfile(mapper.getTraineeMapper().toEntity(trainee));
    }

    @AuthRequired
    public void updateTrainee(@Valid TraineeDTO trainee) {
        traineeService.updateTraineeProfile(mapper.getTraineeMapper().toEntity(trainee));
    }

    @AuthRequired
    public void deleteTraineeById(long traineeId) {
        traineeService.deleteTraineeProfileById(traineeId);
    }

    @AuthRequired
    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeProfileByUsername(username);
    }

    @AuthRequired
    public TraineeDTO getTraineeById(long traineeId) {
        return mapper.getTraineeMapper().toDTO(traineeService.selectTraineeProfileById(traineeId));
    }

    @AuthRequired
    public TraineeDTO getTraineeByUsername(String username) {
        return mapper.getTraineeMapper().toDTO(traineeService.selectTraineeProfileByUsername(username));
    }

    @AuthRequired
    public void activateTrainee(long traineeId) {
        traineeService.activateTraineeProfile(traineeId);
    }

    @AuthRequired
    public void deactivateTrainee(long traineeId) {
        traineeService.deactivateTraineeProfile(traineeId);
    }

    @AuthRequired
    public List<TrainingDTO> getTrainingsForTrainee(String username, LocalDate fromDate, LocalDate toDate,
                                                    String trainerName, String trainingTypeName) {
        List<Training> trainingList =
                traineeService.getTrainingsForTrainee(username, fromDate, toDate, trainerName, trainingTypeName);

        return trainingList.stream()
                .map(tr -> mapper.getTrainingMapper().toDTO(tr))
                .toList();
    }

    @AuthRequired
    public List<TrainerDTO> getTrainersNotAssignedToTrainee(String username) {
        List<Trainer> trainerList = traineeService.getTrainersNotAssignedToTrainee(username);
        return trainerList.stream()
                .map(tr -> mapper.getTrainerMapper().toDTO(tr))
                .toList();
    }

    @AuthRequired
    public void updateTraineeListOfTrainers(long traineeId, List<String> trainerUsernames) {
        traineeService.updateTraineeListOfTrainers(traineeId, trainerUsernames);
    }

    public void createTrainer(@Valid TrainerDTO trainer) {
        trainerService.createTrainerProfile(mapper.getTrainerMapper().toEntity(trainer));
    }

    @AuthRequired
    public void updateTrainer(@Valid TrainerDTO trainer) {
        trainerService.updateTrainerProfile(mapper.getTrainerMapper().toEntity(trainer));
    }

    @AuthRequired
    public TrainerDTO getTrainerById(long trainerId) {
        return mapper.getTrainerMapper().toDTO(trainerService.selectTrainerProfileById(trainerId));
    }

    @AuthRequired
    public TrainerDTO getTrainerByUsername(String username) {
        return mapper.getTrainerMapper().toDTO(trainerService.selectTrainerProfileByUsername(username));
    }

    @AuthRequired
    public void activateTrainer(long trainerId) {
        trainerService.activateTrainerProfile(trainerId);
    }

    @AuthRequired
    public void deactivateTrainer(long trainerId) {
        trainerService.deactivateTrainerProfile(trainerId);
    }

    @AuthRequired
    public List<TrainingDTO> getTrainingsForTrainer(String username, LocalDate fromDate, LocalDate toDate,
                                                 String traineeName) {
        List<Training> trainingList =
                trainerService.getTrainingsForTrainer(username, fromDate, toDate, traineeName);

        return trainingList.stream()
                .map(tr -> mapper.getTrainingMapper().toDTO(tr))
                .toList();
    }

    @AuthRequired
    public TrainingDTO getTraining(long trainingId) {
        return mapper.getTrainingMapper().toDTO(trainingService.selectTraining(trainingId));
    }

    @AuthRequired
    public void addTraining(String traineeUsername, String trainerUsername,
                            String trainingName, LocalDate date, int duration) {
        trainingService.addTraining(traineeUsername, trainerUsername, trainingName, date, duration);
    }

}
