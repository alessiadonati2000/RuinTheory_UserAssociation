import java.util.*;

public class AlgoritmAssociation extends Association{

    public AlgoritmAssociation(List<User> user, List<Server> server){
        this.users = user;
        this.servers = server;
        this.associationMatrix = new int[user.size()][server.size()];
        this.elaboration = new Elaboration();
    }

    public void associationUserServer(List<User> users, List<Server> servers, Elaboration elaboration) {
        inizializeAM();

        System.out.println("//////////////////////////////////////////////////////////////////\n");
        System.out.println("USER PROPOSED TO BEST SERVER FOR HIM\n");
        for (User user : users) {
            Server bestServer = chooseBestServer(user);
            if (bestServer != null) {
                bestServer.getPropostedUsers().add(user);
            }
        }
        System.out.println("//////////////////////////////////////////////////////////////////\n");

        for (Server server : servers) {
            System.out.println("ELABORATION IN " + server + "\n");
            System.out.println("List of proposed users:\n" + server.getPropostedUsers() + "\n");

            System.out.println("-------------START WITH RUIN PROBABILITY-------------");
            for (User proposedUser : server.getPropostedUsers()){
                Map<User, Double> ruinDegreeMap = elaboration.associateUserRuinDegree(proposedUser, server);
                System.out.println(proposedUser + " Ruin degree: " + ruinDegreeMap.get(proposedUser));
            }
            System.out.println("-------------------------END-------------------------\n");

            List<User> priorityList = elaboration.buildPriorityList(server);
            System.out.println("Priority list:\n" + priorityList + "\n");

            System.out.println("START ASSOCIATION TASK-BUFFER");
            for (User user : priorityList) {
                System.out.println(user);

                if (server.getBuffer() >= user.getTask()) {
                    setValueAM(users.indexOf(user), servers.indexOf(server), 1);
                    server.reduceBuffer(user.getTask());

                    System.out.println("Update buffer: " + (int) server.getBuffer() + "\n");

                } else {
                    System.out.println("ERROR: BUFFER EXAUSTED");

                    Server newServer = chooseSecondBestServer(user, server);
                    if (newServer != null) {
                        newServer.getPropostedUsers().add(user);

                        if (newServer.getBuffer() >= user.getTask()) {
                            setValueAM(users.indexOf(user), servers.indexOf(newServer), 1);
                            newServer.reduceBuffer(user.getTask());
                            System.out.println("Update buffer: " + (int) newServer.getBuffer() + "\n");
                        }
                    }
                }
            }
            System.out.print("//////////////////////////////////////////////////////////////////\n");
            System.out.println();
        }
    }

    private Server chooseBestServer(User user) {
        Server bestServer = null;
        double bestSNR = Double.NEGATIVE_INFINITY;

        for (Server server : servers) {
            List<Match> snr = elaboration.calculateSNR(user, server);
            double snr_value = elaboration.getSNR(user, server, snr);
            System.out.println(user + " " + server + " SNR: " + (int) snr_value);
            if (snr_value > bestSNR) {
                bestSNR = snr_value;
                bestServer = server;
            }
        }

        System.out.println("User choose: " + bestServer);
        System.out.println();
        return bestServer;
    }

    private Server chooseSecondBestServer(User user, Server serverActual) {
        Server secondBestServer = null;
        double bestSNR = Double.NEGATIVE_INFINITY;

        for (Server server : servers) {
            if (server != serverActual) {
                List<Match> snr = elaboration.calculateSNR(user, server);
                double snr_value = elaboration.getSNR(user, server, snr);
                if (snr_value > bestSNR) {
                    bestSNR = snr_value;
                    secondBestServer = server;
                }
            }
        }

        System.out.println("User choose: " + secondBestServer);
        System.out.println();
        return secondBestServer;
    }
}
