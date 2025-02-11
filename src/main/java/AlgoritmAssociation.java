import java.util.*;

public class AlgoritmAssociation extends Association{

    public AlgoritmAssociation(List<User> user, List<Server> server, Elaboration elaboration){
        this.users = user;
        this.servers = server;
        this.associationMatrix = new int[user.size()][server.size()];
        this.elaboration = elaboration;
        this.totalUnusedBuffer = 0.0;
        this.totalSystemTime = 0.0;
    }

    public void associationUserServer(List<User> users, List<Server> servers) {
        inizializeAM();

        System.out.println("//////////////////////////////////////////////////////////////////\n");
        System.out.println("USER PROPOSED TO BEST SERVER FOR HIM\n");

        Map<User, Server> initialAssignments = new HashMap<>();

        for (User user : users) {
            Server bestServer = chooseBestServer(user);
            if (bestServer != null) {
                bestServer.getProposedUsers().add(user);
                initialAssignments.put(user, bestServer);
            }
        }
        System.out.println("//////////////////////////////////////////////////////////////////\n");

        List<User> unallocatedUsers = new ArrayList<>();

        for (Server server : servers) {
            System.out.println("ELABORATION IN " + server + "\n");
            System.out.println("List of proposed users:\n" + server.getProposedUsers() + "\n");

            System.out.println("----------------------------CALCULATE TRANSMISSION TIME----------------------------");
            for (User proposedUser : server.getProposedUsers()) {
                double transmissionTime_value = elaboration.calculateTransmissionTime(proposedUser, server, 0);
                System.out.println(proposedUser + " " + server + " Transmition time: " + transmissionTime_value);
            }
            System.out.println("-----------------------------------------END---------------------------------------\n");

            System.out.println("----------------------------CALCULATE COMPUTING TIME----------------------------");
            for (User proposedUser : server.getProposedUsers()) {
                double computationTime_value = elaboration.calculateComputationTime(proposedUser, server, 0);
                System.out.println(proposedUser + " " + server + " Computation time: " + computationTime_value);
            }
            System.out.println("-----------------------------------------END---------------------------------------\n");

            System.out.println("----------------------------CALCULATE LOCAL COMPUTING TIME----------------------------");
            for (User proposedUser : server.getProposedUsers()) {
                double localComputationTime_value = elaboration.calculateLocalComputationTime(proposedUser, server, 0);
                System.out.println(proposedUser + " " + server + " Local Computation time: " + localComputationTime_value);
            }
            System.out.println("-----------------------------------------END---------------------------------------\n");

            System.out.println("-------------START WITH RUIN PROBABILITY-------------");
            for (User proposedUser : server.getProposedUsers()){
                Map<User, Double> ruinDegreeMap = elaboration.associateUserRuinDegree(proposedUser, server);
                System.out.println(proposedUser + " Ruin degree: " + ruinDegreeMap.get(proposedUser));
            }
            System.out.println("-------------------------END-------------------------\n");


            List<User> priorityList = elaboration.buildPriorityList(server);
            System.out.println("Priority list:\n" + priorityList + "\n");

            System.out.println("START ASSOCIATION TASK-BUFFER");
            for (User user : priorityList) {
                double transmissionTime = elaboration.getList_value(user, server, elaboration.getTransmissionTime_listAlgoritm());
                totalSystemTime += transmissionTime;

                if (server.getBuffer() >= user.getTask()) {
                    setValueAM(users.indexOf(user), servers.indexOf(server), 1);
                    server.reduceBuffer(user.getTask());

                    double computationTime = elaboration.getList_value(user, server, elaboration.getComputationTime_listAlgoritm());
                    totalSystemTime += computationTime;

                    System.out.println("Update buffer: " + (int) server.getBuffer() + "\n");

                } else {
                    double localComputationTime = elaboration.getList_value(user, server, elaboration.getLocalComputationTime_listAlgoritm());
                    totalSystemTime += localComputationTime * 0.8; // Penalizziamo meno il calcolo locale
                    unallocatedUsers.add(user);
                    System.out.println("ERROR: BUFFER EXHAUSTED");
                }
            }

            totalUnusedBuffer += server.getBuffer();
            System.out.println("buffer non utilizzato: " + (int) totalUnusedBuffer);

            System.out.print("//////////////////////////////////////////////////////////////////\n");
            System.out.println();
        }

        // Tentativo di riallocazione per gli utenti rifiutati
        System.out.println("\n\nSTART REALLOCATION PROCESS FOR UNALLOCATED USERS\n");

        for (User user : unallocatedUsers) {
            System.out.println("Reallocating user: " + user);
            Server newServer = chooseBestServer(user);

            if (newServer != null && newServer.getBuffer() >= user.getTask()) {
                double newSNR = elaboration.calculateSNR(user, newServer);
                System.out.println("Recalculated SNR for " + user + " on " + newServer + ": " + (int) newSNR);
                elaboration.calculateTransmissionTime(user, newServer, 0);
                elaboration.calculateComputationTime(user, newServer, 0);
                elaboration.calculateLocalComputationTime(user, newServer, 0);

                newServer.reduceBuffer(user.getTask());
                setValueAM(users.indexOf(user), servers.indexOf(newServer), 1);

                double transmissionTime = elaboration.getList_value(user, newServer, elaboration.getTransmissionTime_listAlgoritm());
                totalSystemTime += transmissionTime;

                double computationTime = elaboration.getList_value(user, newServer, elaboration.getComputationTime_listAlgoritm());
                totalSystemTime += computationTime;

                System.out.println("User " + user + " reassigned to " + newServer);

            } else if (newServer == null) {
                System.out.println("FAILED to reallocate " + user + ". Task will be computed locally.");
            } else {
                double localComputationTime = elaboration.getList_value(user, newServer, elaboration.getLocalComputationTime_listAlgoritm());
                totalSystemTime += localComputationTime * 0.8;
                System.out.println("FAILED to reallocate " + user + ". Task will be computed locally.");
            }
        }

        System.out.println("\nREALLOCATION PROCESS ENDED\n");

        // Reset dei server
        for (Server s : servers) {
            s.setBuffer((int) s.getOriginalBuffer());
        }
    }

    private Server chooseBestServer(User user) {
        Server bestServer = null;
        double bestMetric  = Double.NEGATIVE_INFINITY;

        for (Server server : servers) {
            double snr_value = elaboration.calculateSNR(user, server);
            double bufferAvailability = server.getBuffer() - user.getTask();
            System.out.println(user + " " + server + " SNR: " + (int) snr_value);

            if (bufferAvailability >= 0) {
                double metric = snr_value / (1 + Math.abs(bufferAvailability)); // Penalizza server con poco buffer

                if (metric > bestMetric) {
                    bestMetric = metric;
                    bestServer = server;
                }
            }
        }

        System.out.println("User choose: " + bestServer);
        System.out.println();
        return bestServer;
    }


}
