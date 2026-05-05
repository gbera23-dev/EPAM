package entities;

import java.util.Date;

public class Training implements GymEntity {

    private long trainingPK;
    private long traineeId;
    private long trainerId;
    private String name;
    private TrainingType trainingType;
    private Date date;
    private int duration;

    public Training(long trainingPK, long traineeId, long trainerId, String name,
                    TrainingType trainingType, Date date, int duration) {
        this.trainingPK = trainingPK;
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.name = name;
        this.trainingType = trainingType;
        this.date = date;
        this.duration = duration;
    }

    public long getTraineeId() {
        return traineeId;
    }

    public long getTrainingPK() {
        return trainingPK;
    }

    public void setTrainingPK(long trainingPK) {
        this.trainingPK = trainingPK;
    }

    public void setTraineeId(int traineeId) {
        this.traineeId = traineeId;
    }

    public long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
