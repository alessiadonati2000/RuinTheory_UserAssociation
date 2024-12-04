import java.util.*;

public class Elaboration {
    private User user;
    private Server server;
    final double bandwidth = 20 * Math.pow(10, 6);
    private Map<User, Double> computationTime;

    public Elaboration() {}

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public List<Match> calculateSNR(User user, Server server) {
        List<Match> snr = new ArrayList<>();
        double snr_value = Math.random()*1500;
        snr.add(new Match(user, server, snr_value));

        return snr;
    }

    public double getSNR(User user, Server server, List<Match> snr) {
        for (Match match : snr) {
            if (match.getUser().equals(user) && match.getServer().equals(server)) {
                return match.getValue();
            }
        }
        throw new IllegalArgumentException("No corresponding value found");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public List<Match> calculateTransmissionTime(User user, Server server, List<Match> snr) {
        double transmissionTime_value = 0.0;
        double uplinkDataRate = 0.0;
        List<Match> transmissionTime = new ArrayList<>();

        uplinkDataRate = (bandwidth / server.getPropostedUsers().size()) * (Math.log(1 + getSNR(user, server, snr)) / Math.log(2));
        transmissionTime_value = user.getTask() / uplinkDataRate;
        transmissionTime.add(new Match(user, server, transmissionTime_value));

        return transmissionTime;
    }

    public double getTransmissionTime(User user, Server server, List<Match> transmissionTime) {
        for (Match match : transmissionTime) {
            if (match.getUser().equals(user) && match.getServer().equals(server)) {
                return match.getValue();
            }
        }
        throw new IllegalArgumentException("No corresponding value found");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static int fattoriale(int j) {
        if (j == 0 || j == 1) {
            return 1;
        } else {
            return (j * fattoriale(j - 1));
        }
    }

    private double calculateRuinProbability(Server server, double time) {
        double ruinProbability = 0.0;

        double sumTask = 0;
        for (User _user : server.getPropostedUsers()){
            sumTask += _user.getTask();
        }

        double term = 0.0;
        term = (server.getBuffer() - sumTask)/( (server.COMPUTING_CAPACITY * time)/ server.CPU_CYCLExBIT );
        ruinProbability = 1 / (1 + Math.exp(term));

        return ruinProbability;
    }

    private double calculateRuinDegree(User user, Server server){
        double ruinDegree = 0.0;
        ruinDegree = user.getTask() / calculateRuinProbability(server, 0.1);
        return ruinDegree;
    }

    public Map<User, Double> associateUserRuinDegree(User user, Server server) {
        Map<User, Double> ruinDegreeMap = new HashMap<>();
        double ruinDegree = calculateRuinDegree(user, server);
        ruinDegreeMap.put(user, ruinDegree);
        return ruinDegreeMap;
    }

    public List<User> buildPriorityList(Server server) {
        server.getPropostedUsers().sort(Comparator.comparing(user -> associateUserRuinDegree(user, server).get(user)));
        List<User> priorityList = server.getPropostedUsers();
        return priorityList;
    }
}
