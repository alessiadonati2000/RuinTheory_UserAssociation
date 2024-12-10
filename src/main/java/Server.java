import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Server {
    private double buffer;
    private double originalBuffer;
    final double COMPUTING_CAPACITY = 6 * Math.pow(10, 5);
    final int CPU_CYCLExBIT = 10;
    private List<User> propostedUsers;

    public Server() {}

    public Server(double buffer) {
        this.buffer = buffer;
        this.originalBuffer = buffer;
        this.propostedUsers = new ArrayList<User>();
    }

    public double getBuffer() {
        return buffer;
    }

    public double getOriginalBuffer() {
        return originalBuffer;
    }

    public List<User> getPropostedUsers() {
        return propostedUsers;
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

    public void reduceBuffer(double task) {
        if (this.buffer >= task) {
            this.buffer -= task;
        } else {
            System.out.printf("Buffer's server cannot support this task");
        }
    }

    public void setBuffer(double buffer) {
        this.buffer = buffer;
    }

    @Override
    public String toString() {
        return "Server (Buffer: " + (int) buffer + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == Server.class) {
            if(((Server) obj).getBuffer() == this.buffer) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(buffer);
    }

}
