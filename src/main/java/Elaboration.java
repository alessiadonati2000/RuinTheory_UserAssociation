import java.util.*;

public class Elaboration {
    private List<Match> snr_list;
    private List<Match> transmissionTime_list;

    final double bandwidth;

    public Elaboration() {
        this.snr_list = new ArrayList<>();
        this.transmissionTime_list = new ArrayList<>();
        this.bandwidth = 20 * Math.pow(10, 6);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public double calculateSNR(User user, Server server) {
        double snr_value = Math.random()*1500;
        snr_list.add(new Match(user, server, snr_value));
        return snr_value;
    }

    public double getSNR_value(User user, Server server, List<Match> snr) {
        snr = getSNR_list();
        for (Match match : snr) {
            if (match.getUser().equals(user) && match.getServer().equals(server)) {
                return match.getValue();
            }
        }
        throw new IllegalArgumentException("No corresponding value found");
    }

    public List<Match> getSNR_list() {
        return snr_list;
    }

    public void printSNRList() {
        System.out.println(snr_list);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public double calculateTransmissionTime(User user, Server server, List<Match> snr_list) {
        double transmissionTime_value = 0.0;
        double uplinkDataRate = 0.0;

        double snr = getSNR_value(user, server, snr_list);
        System.out.println("SNR: " + snr);
        double var = (Math.log(1 + snr) / Math.log(2));
        System.out.println(var);
        System.out.println(bandwidth);
        System.out.println(server.getPropostedUsers().size());

        uplinkDataRate = (bandwidth / server.getPropostedUsers().size()) * (Math.log(1 + snr) / Math.log(2));
        System.out.println(uplinkDataRate);

        transmissionTime_value = user.getTask() / uplinkDataRate;
        transmissionTime_list.add(new Match(user, server, transmissionTime_value));

        return transmissionTime_value;
    }

    public double getTransmissionTime_value(User user, Server server, List<Match> transmissionTime_list) {
        for (Match match : transmissionTime_list) {
            if (match.getUser().equals(user) && match.getServer().equals(server)) {
                return match.getValue();
            }
        }
        throw new IllegalArgumentException("No corresponding value found");
    }

    public List<Match> getTransmissionTime_list() {
        return transmissionTime_list;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private double calculateComputationTime(User user, Server server) {
        double computationTime = 0.0;
        computationTime = (server.CPU_CYCLExBIT * user.getTask()) / server.COMPUTING_CAPACITY;
        return computationTime;
    }

    public Map<User, Double> associateUserComputingTime(User user, Server server) {
        Map<User, Double> computingTimeMap = new HashMap<>();
        double computationTime = calculateComputationTime(user, server);
        computingTimeMap.put(user, computationTime);
        return computingTimeMap;
    }

    public Map<User, Double> associateUserTotalSystemTime(User user, Server server) {
        Map<User, Double> totalSystemTimeMap = new HashMap<>();
        double totalSystemTime = calculateComputationTime(user, server) + getTransmissionTime_value(user, server, transmissionTime_list);
        totalSystemTimeMap.put(user, totalSystemTime);
        return totalSystemTimeMap;
    }

    public List<User> buildPriorityListTotalTime(Server server) {
        server.getPropostedUsers().sort(Comparator.comparing(user -> associateUserTotalSystemTime(user, server).get(user)));
        List<User> priorityList = server.getPropostedUsers();
        return priorityList;
    }
}
