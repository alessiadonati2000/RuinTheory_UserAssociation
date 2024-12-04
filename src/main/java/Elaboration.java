import java.util.*;

public class Elaboration {
    private User user;
    private Server server;
    final double bandwidth = 20 * Math.pow(10, 6);
    private Map<User, Double> ruinProbability;
    private Map<User, Double> computationTime;
    private List<Match> snr;
    private List<Match> transmissionTime;

    public Elaboration() {}

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

    static int fattoriale(int j) {
        if (j == 0 || j == 1) {
            return 1;
        } else {
            return (j * fattoriale(j - 1));
        }
    }

    private double calculateRuinProbability(double time) {
        //TODO implementare per bene il calcolo dell aprob fi rovina, magari dividendo i due calcoli
        double ruinProbability = 0.0;

        double sumTask = 0;
        for (User _user : server.getPropostedUsers()){
            sumTask += _user.getTask();
        }

        double term = 0.0;
        term = (server.getBuffer() - sumTask)/( (server.COMPUTING_CAPACITY * time)/ server.CPU_CYCLExBIT );
        ruinProbability = 1 / (1 + Math.exp(term));

        System.out.printf("Probabilità di rovina: %.10f\n", probabilitaRovina);
        double gradoRovina = utente.getDimTask() / probabilitaRovina;

        if (Double.isNaN(probabilitaRovina) || Double.isInfinite(probabilitaRovina) || Double.isInfinite(gradoRovina)){
            System.exit(0);
        }

        return gradoRovina;
    }
}
