package entities;


public class Trainer implements GymEntity {

    private long trainerPK;
    private String specialization;
    private User user;

    public long getTrainerPK() {
        return trainerPK;
    }

    public void setTrainerPK(long trainerPK) {
        this.trainerPK = trainerPK;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
