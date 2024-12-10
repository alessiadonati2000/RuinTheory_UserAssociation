import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private double task;
    private double transmissionPower;

    public User(){}

    public User(double task, double transmissionPower) {
        this.task = task;
        this.transmissionPower = transmissionPower;
    }

    public double getTask() {
        return task;
    }

    public double getTransmissionPower() {
        return transmissionPower;
    }

    public double calculateTask(double min, double max){
        return ((Math.random() * (max - min + 1)) + min)*8;
    }

    public double calculateTransmissionPower(double min, double max){
        return (min + (Math.random() * max));
    }

    public List<User> generateUsers(int numUsers, int maxTask, int minTask, double maxTPower, double minTPower) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < numUsers; i++) {
            User user = new User(calculateTask(minTask, maxTask), calculateTransmissionPower(minTPower, maxTPower));
            users.add(user);
        }
        return users;
    }

    @Override
    public String toString() {
        return "User (Task: " + (int) task + ")";
    }

    @Override
    public boolean equals(Object obj) {
        // obj è lo user che devo andare a cercare nella lista

        // controllare che obj sia uno User
        // facendo il casting a User, estrarre i valori per il matching e confrontarli con quelli dell'oggetto che invoca equals, cioè this.
        // ritornare true o false
        if (obj.getClass() == User.class) {
            if(((User) obj).getTask() == task) {
                return true;
            }
        }
        return false;
        /*
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(this.task, user.task);
        */
    }

    @Override
    public int hashCode() {
        return Objects.hash(task);
    }

}
