// this class contains the functions that perform the calculations that will allow us to make the algorithm work and evaluate it

import java.util.*;

public class Elaboration {
    private Map<User, Double> ruinDegreeMap;

    private List<Match> snr_list;

    private List<Match> transmissionTime_listAlgoritm;
    private List<Match> transmissionTime_listRandom;
    private List<Match> computationTime_listAlgoritm;
    private List<Match> computationTime_listRandom;
    private List<Match> localComputationTime_listAlgoritm;
    private List<Match> localComputationTime_listRandom;

    private List<Match> transmissionEnergy_listAlgoritm;
    private List<Match> transmissionEnergy_listRandom;
    private List<Match> computationEnergy_listAlgoritm;
    private List<Match> computationEnergy_listRandom;
    private List<Match> localEnergy_listAlgoritm;
    private List<Match> localEnergy_listRandom;

    final double BANDWIDTH = 20 * Math.pow(10, 6); // Hz
    final double COSTANT_CHIP = 1e-28;

    public Elaboration() {
        this.ruinDegreeMap = new HashMap<>();
        this.snr_list = new ArrayList<>();
        this.transmissionTime_listAlgoritm = new ArrayList<>();
        this.transmissionTime_listRandom = new ArrayList<>();
        this.computationTime_listAlgoritm = new ArrayList<>();
        this.computationTime_listRandom = new ArrayList<>();
        this.localComputationTime_listAlgoritm = new ArrayList<>();
        this.localComputationTime_listRandom = new ArrayList<>();
        this.transmissionEnergy_listAlgoritm = new ArrayList<>();
        this.transmissionEnergy_listRandom = new ArrayList<>();
        this.computationEnergy_listAlgoritm = new ArrayList<>();
        this.computationEnergy_listRandom = new ArrayList<>();
        this.localEnergy_listAlgoritm = new ArrayList<>();
        this.localEnergy_listRandom = new ArrayList<>();
    }

    public double getList_value(User user, Server server, List<Match> list) {
        for (Match match : list) {
            double userMatchTask = match.getUser().getTask();
            double serverMatchBuffer = match.getServer().getOriginalBuffer();
            if((int) userMatchTask == (int) user.getTask() && (int) serverMatchBuffer == (int) server.getOriginalBuffer()) {
                return match.getValue();
            }
        }
        throw new IllegalArgumentException("No corresponding value found");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public double calculateSNR(User user, Server server) {
        // SNR is calculated in a randomic way because i did not implement the concept of distance between users and servers
        // Higher SNR is, best connection between user and server is
        double snr_value = Math.random() * 1500;
        System.out.println("SNR between " + user + " and " + server + ": " + (int) snr_value);
        snr_list.add(new Match(user, server, snr_value));
        return snr_value;
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
    // TRANSMISSION

    public void calculateTransmissionTime(User user, Server server, int flag) {
        double uplinkDataRate = (BANDWIDTH / server.getProposedUsers().size()) * (Math.log(1 + getList_value(user, server, snr_list)) / Math.log(2));
        double transmissionTime_value = user.getTask() / uplinkDataRate;

        if (flag == 0) {
            transmissionTime_listAlgoritm.add(new Match(user, server, transmissionTime_value));
        } else if (flag == 1) {
            transmissionTime_listRandom.add(new Match(user, server, transmissionTime_value));
        }
    }

    public void calculateTransmissionEnergy(User user, Server server, int flag) {
       if (flag == 0) {
            transmissionEnergy_listAlgoritm.add(new Match(user, server, user.getTransmissionPower() * getList_value(user, server, transmissionTime_listAlgoritm)));
        } else if (flag == 1) {
            transmissionEnergy_listRandom.add(new Match(user, server, user.getTransmissionPower() * getList_value(user, server, transmissionTime_listRandom)));
        }
    }

    public List<Match> getTransmissionTime_listAlgoritm() {
        return transmissionTime_listAlgoritm;
    }

    public List<Match> getTransmissionTime_listRandom() {
        return transmissionTime_listRandom;
    }

    public List<Match> getTransmissionEnergy_listAlgoritm() {
        return transmissionEnergy_listAlgoritm;
    }

    public List<Match> getTransmissionEnergy_listRandom() {
        return transmissionEnergy_listRandom;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // REMOTE COMPUTATION

    public void calculateComputationTime(User user, Server server, int flag) {
        double computationTime_value = (server.CPU_CYCLExBIT * user.getTask()) / server.COMPUTING_CAPACITY;

        if (flag == 0) {
            computationTime_listAlgoritm.add(new Match(user, server, computationTime_value));
        } else if (flag == 1) {
            computationTime_listRandom.add(new Match(user, server, computationTime_value));
        }
    }

    public void calculateComputationEnergy(User user, Server server, int flag) {
        double computationEnergy = COSTANT_CHIP * Math.pow(server.COMPUTING_CAPACITY, 2) * server.CPU_CYCLExBIT * user.getTask();

        if (flag == 0) {
            computationEnergy_listAlgoritm.add(new Match(user, server, computationEnergy));
        } else if (flag == 1) {
            computationEnergy_listRandom.add(new Match(user, server, computationEnergy));
        }
    }

    public List<Match> getComputationTime_listAlgoritm(){
        return computationTime_listAlgoritm;
    }

    public List<Match> getComputationTime_listRandom(){
        return computationTime_listRandom;
    }

    public List<Match> getComputationEnergy_listAlgoritm(){
        return computationEnergy_listAlgoritm;
    }

    public List<Match> getComputationEnergy_listRandom(){
        return computationEnergy_listRandom;
    }

    public void printComputationTimeListAlgoritm() {
        for (int i = 0; i < getComputationTime_listAlgoritm().size(); i++) {
            System.out.println(computationTime_listAlgoritm.get(i).getUser() + " " + computationTime_listAlgoritm.get(i).getServer() + " Computation Time: " + computationTime_listAlgoritm.get(i).getValue());
        }
    }

    public void printComputationTimeListRandom() {
        for (int i = 0; i < getComputationTime_listRandom().size(); i++) {
            System.out.println(computationTime_listRandom.get(i).getUser() + " " + computationTime_listRandom.get(i).getServer() + " Computation Time: " + computationTime_listRandom.get(i).getValue());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // LOCAL COMPUTATION

    public void calculateLocalComputationTime(User user, Server server, int flag){
        double localComputationTime_value = (user.CPU_CYCLExBIT * user.getTask()) / user.LOCAL_COMPUTING_CAPACITY;

        if (flag == 0) {
            localComputationTime_listAlgoritm.add(new Match(user, server, localComputationTime_value));
        } else if (flag == 1) {
            localComputationTime_listRandom.add(new Match(user, server, localComputationTime_value));
        }
    }

    public void calculateLocalEnergy(User user, Server server, int flag) {
        double localEnergy = COSTANT_CHIP * Math.pow(user.LOCAL_COMPUTING_CAPACITY, 2) * server.CPU_CYCLExBIT * user.getTask();

        if (flag == 0) {
            localEnergy_listAlgoritm.add(new Match(user, server, localEnergy));
        } else if (flag == 1) {
            localEnergy_listRandom.add(new Match(user, server, localEnergy));
        }
    }

    public double calculateLocalComputationTimeWithoutServer(User user){
        return (user.CPU_CYCLExBIT * user.getTask()) / user.LOCAL_COMPUTING_CAPACITY;
    }

    public double calculateLocalEnergyWithoutServer(User user){
        return COSTANT_CHIP * Math.pow(user.LOCAL_COMPUTING_CAPACITY, 2) * user.CPU_CYCLExBIT * user.getTask();
    }

    public List<Match> getLocalComputationTime_listAlgoritm(){
        return localComputationTime_listAlgoritm;
    }

    public List<Match> getLocalComputationTime_listRandom(){
        return localComputationTime_listRandom;
    }

    public List<Match> getLocalEnergy_listAlgoritm(){
        return localEnergy_listAlgoritm;
    }

    public List<Match> getLocalEnergy_listRandom(){
        return localEnergy_listRandom;
    }

    public void printLocalComputationTimeListAlgoritm() {
        for (int i = 0; i < getLocalComputationTime_listAlgoritm().size(); i++) {
            System.out.println(localComputationTime_listAlgoritm.get(i).getUser() + " " + localComputationTime_listAlgoritm.get(i).getServer() + " Local Computation Time: " + localComputationTime_listAlgoritm.get(i).getValue());
        }
    }

    public void printLocalComputationTimeListRandom() {
        for (int i = 0; i < getLocalComputationTime_listRandom().size(); i++) {
            System.out.println(localComputationTime_listRandom.get(i).getUser() + " " + localComputationTime_listRandom.get(i).getServer() + " Local Computation Time: " + localComputationTime_listRandom.get(i).getValue());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private double calculateRuinProbability(Server server, double time) {
        // Using a sigmoide to model probability, different from the paper
        double totalArrivalData = 0;
        for (User user : server.getProposedUsers()){
            totalArrivalData += user.getTask();
        }
        double initialSurplus = server.getBuffer();

        double term = (initialSurplus - totalArrivalData) / ((server.COMPUTING_CAPACITY * time) / server.CPU_CYCLExBIT);
        double ruinProbability = 1 / (1 + Math.exp(term));

        // To limit the ruin probability to 1
        ruinProbability = Math.min(ruinProbability, 1);

        return ruinProbability;
    }

    public double calculateRuinDegree(User user, Server server) {
        double ruinProbability = calculateRuinProbability(server, 0.1);

        // To avoid a division by 0
        if (ruinProbability == 0) {
            ruinProbability = 0.01;
        }

        double ruinDegree = user.getTask() / ruinProbability;
        ruinDegreeMap.put(user, ruinDegree);

        return ruinDegree;
    }

    public Map<User, Double> associateUserRuinDegree(User user, Server server) {
        Map<User, Double> ruinDegreeMap = new HashMap<>();
        double ruinDegree = calculateRuinDegree(user, server);
        ruinDegreeMap.put(user, ruinDegree);
        return ruinDegreeMap;
    }

    public List<User> buildPriorityList(Server server) {
        server.getProposedUsers().sort(Comparator.comparing(user -> associateUserRuinDegree(user, server).get(user)));
        return server.getProposedUsers();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<User> sortUnallocatedUsersByTask(List<User> unallocatedUsers) {
        unallocatedUsers.sort(Comparator.comparing(User::getTask));
        return unallocatedUsers;
    }

}
