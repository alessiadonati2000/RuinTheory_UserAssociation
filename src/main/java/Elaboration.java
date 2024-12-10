import java.util.*;

public class Elaboration {
    private List<Match> snr_list;
    private List<Match> transmissionTime_listAlgoritm;
    private List<Match> transmissionTime_listRandom;
    private List<Match> computingTime_list;

    final double bandwidth;

    public Elaboration() {
        this.snr_list = new ArrayList<>();
        this.transmissionTime_listAlgoritm = new ArrayList<>();
        this.transmissionTime_listRandom = new ArrayList<>();
        this.bandwidth = 20 * Math.pow(10, 6);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public double calculateSNR(User user, Server server) {
        double snr_value = Math.random()*1500;
        snr_list.add(new Match(user, server, snr_value));
        return snr_value;
    }

    public double getSNR_value(User user, Server server) {
        for (Match match : getSNR_list()) {
            double userMatchTask = match.getUser().getTask();
            double serverMatchBuffer = match.getServer().getBuffer();
            if((int) userMatchTask == (int) user.getTask() && (int) serverMatchBuffer == (int) server.getBuffer()) {
                return match.getValue();
            }
        }
        throw new IllegalArgumentException("No corresponding value found");
    }

    public List<Match> getSNR_list() {
        return snr_list;
    }

    public void printSNRList() {
        for (int i = 0; i < getSNR_list().size(); i++) {
            System.out.println(snr_list.get(i).getUser() + " " + snr_list.get(i).getServer() + " SNR: " + (int) snr_list.get(i).getValue());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public double calculateTransmissionTime(User user, Server server, int flag) {
        double transmissionTime_value = 0.0;
        double uplinkDataRate = 0.0;

        uplinkDataRate = (bandwidth / server.getPropostedUsers().size()) * (Math.log(1 + getSNR_value(user, server)) / Math.log(2));
        transmissionTime_value = user.getTask() / uplinkDataRate;

        if (flag == 0) {
            transmissionTime_listAlgoritm.add(new Match(user, server, transmissionTime_value));
        } else if (flag == 1) {
            transmissionTime_listRandom.add(new Match(user, server, transmissionTime_value));
        }


        return transmissionTime_value;
    }

    public double getTransmissionTime_value(User user, Server server, List<Match> transmissionTime_list) {
        for (Match match : transmissionTime_list) {
            double userMatchTask = match.getUser().getTask();
            double serverMatchBuffer = match.getServer().getBuffer();
            if((int) userMatchTask == (int) user.getTask() && (int) serverMatchBuffer == (int) server.getBuffer()) {
                return match.getValue();
            }
        }
        throw new IllegalArgumentException("No corresponding value found");
    }

    public List<Match> getTransmissionTime_listAlgoritm() {
        return transmissionTime_listAlgoritm;
    }

    public List<Match> getTransmissionTime_listRandom() {
        return transmissionTime_listRandom;
    }

    public void printTransmissionTimeListAlgoritm() {
        for (int i = 0; i < getTransmissionTime_listAlgoritm().size(); i++) {
            System.out.println(transmissionTime_listAlgoritm.get(i).getUser() + " " + transmissionTime_listAlgoritm.get(i).getServer() + " Transmission Time: " + transmissionTime_listAlgoritm.get(i).getValue());
        }
    }

    public void printTransmissionTimeListRandom() {
        for (int i = 0; i < getTransmissionTime_listRandom().size(); i++) {
            System.out.println(transmissionTime_listRandom.get(i).getUser() + " " + transmissionTime_listRandom.get(i).getServer() + " Transmission Time: " + transmissionTime_listRandom.get(i).getValue());
        }
    }

    public double calculateMeanTransmissionTime(Server server, int flag) {
        double meanTransmissionTime = 0.0;
        double sum = 0.0;
        if (flag == 0){
            for (int i = 0; i < getTransmissionTime_listAlgoritm().size(); i++) {
                if(getTransmissionTime_listAlgoritm().get(i).getServer().getBuffer() == server.getBuffer()) {
                    sum += getTransmissionTime_listAlgoritm().get(i).getValue();
                    meanTransmissionTime = sum / server.getPropostedUsers().size();
                }
            }
        } else if (flag == 1) {
            for (int i = 0; i < getTransmissionTime_listRandom().size(); i++) {
                if(getTransmissionTime_listRandom().get(i).getServer().getBuffer() == server.getBuffer()) {
                    sum += getTransmissionTime_listRandom().get(i).getValue();
                    meanTransmissionTime = sum / server.getPropostedUsers().size();
                }
            }
        }
        return meanTransmissionTime;
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

    /*public Map<User, Double> associateUserTotalSystemTime(User user, Server server) {
        Map<User, Double> totalSystemTimeMap = new HashMap<>();
        double totalSystemTime = calculateComputationTime(user, server) + getTransmissionTime_value(user, server);
        totalSystemTimeMap.put(user, totalSystemTime);
        return totalSystemTimeMap;
    }*/

    /*public List<User> buildPriorityListTotalTime(Server server) {
        server.getPropostedUsers().sort(Comparator.comparing(user -> associateUserTotalSystemTime(user, server).get(user)));
        List<User> priorityList = server.getPropostedUsers();
        return priorityList;
    }*/
}
