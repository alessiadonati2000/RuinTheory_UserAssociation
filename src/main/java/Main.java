import java.util.*;

public class Main {

    public static void main(String[] args) {
        Elaboration elaboration = new Elaboration();

        User user = new User();
        List<User> users = user.generateUsers(10,900, 10000, 0.2, 0.1);

        Server server = new Server();
        List<Server> servers = server.generateServers(3, 140000,150000);

        for(User u : users){
            System.out.printf("User (Task: %.0f):\n", u.getTask());
            for(Server s : servers){
                System.out.printf("Server (Buffer: %.0f):\n", s.getBuffer());

                List<Match> snr = elaboration.calculateSNR(u,s);
                System.out.printf("SNR: %f\n", elaboration.getSNR(u,s, snr));

                List<Match> transmissionTime = elaboration.calculateTransmissionTime(u, s, snr);
                System.out.printf("Transmission Time: %f\n", elaboration.getTransmissionTime(u,s, transmissionTime));

            }
            System.out.println("\n");
        }

        AlgoritmAssociation algoritmAssociation = new AlgoritmAssociation(users, servers);
        algoritmAssociation.associationUserServer(users, servers, elaboration);
        algoritmAssociation.printAM();

        // TODO: per ora funziona l'algortimo, verificare bene il funzionamento stampando un po di valori per bene
        // TODO: implementare random e tempi
    }
}
