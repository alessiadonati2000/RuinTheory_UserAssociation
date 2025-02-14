import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Server {
    private double buffer;
    private double originalBuffer;
    private List<User> proposedUsers;
    final double COMPUTING_CAPACITY = 6 * Math.pow(10, 5);
    final int CPU_CYCLExBIT = 10;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Server() {}

    public Server(double buffer) {
        this.buffer = buffer;
        this.originalBuffer = buffer;
        this.proposedUsers = new ArrayList<User>();
    }

    public double getBuffer() {
        return buffer;
    }

    public void setBuffer(double buffer) {
        this.buffer = buffer;
    }

    public void reduceBuffer(double task) {
        if (this.buffer >= task) {
            this.buffer -= task;
        } else {
            System.out.printf("Buffer's server cannot support this task");
        }
    }

    public double getOriginalBuffer() {
        return originalBuffer;
    }

    public List<User> getProposedUsers() {
        return proposedUsers;
    }

    public double calculateBuffer(double min, double max){
        return ((Math.random() * (max - min + 1)) + min)*8;
    }

    public List<Server> generateServers(int numServers, double minBuffer, double maxBuffer) {
        List<Server> servers = new ArrayList<>();
        for (int i = 0; i < numServers; i++) {
            Server server = new Server(calculateBuffer(minBuffer, maxBuffer));
            servers.add(server);
        }
        return servers;
    }

    @Override
    public String toString() {
        return "Server (Buffer: " + (int) buffer + ")";
    }

}
