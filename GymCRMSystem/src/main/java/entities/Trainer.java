package entities;


public class Trainer implements GymEntity {

    private long trainerPK;
    private String specialization;
    private User user;

    @Override
    public String toString() {
        return "Trainer{" +
                "trainerPK=" + trainerPK +
                ", specialization='" + specialization + '\'' +
                ", user=" + user +
                '}';
    }

    public Trainer(long trainerPK, String specialization, User user) {
        this.trainerPK = trainerPK;
        this.specialization = specialization;
        this.user = user;
    }

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

    @Override
    public long getEntityId() {
        return this.trainerPK;
    }

}
