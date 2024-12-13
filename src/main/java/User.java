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


}
