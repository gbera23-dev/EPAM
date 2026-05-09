package entities;

import org.springframework.stereotype.Component;

import java.util.Date;

public class Trainee implements GymEntity {

    private long TraineePK;
    private Date dateOfBirth;
    private String address;
    private User user;

    @Override
    public String toString() {
        return "Trainee{" +
                "TraineePK=" + TraineePK +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", user=" + user +
                '}';
    }

    public Trainee(long traineePK, Date dateOfBirth, String address, User user) {
        TraineePK = traineePK;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.user = user;
    }

    public long getTraineePK() {
        return TraineePK;
    }

    public void setTraineePK(long traineePK) {
        TraineePK = traineePK;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public long getEntityId() {
        return this.TraineePK;
    }
}
