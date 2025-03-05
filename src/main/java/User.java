import java.util.ArrayList;
import java.util.List;

public class User {
    private double task;
    private double transmissionPower;
    final double LOCAL_COMPUTING_CAPACITY = 7 * Math.pow(10, 4);
    final int CPU_CYCLExBIT = 10;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
        return ((Math.random() * (max - min + 1)) + min)*8; // So i have bits
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
