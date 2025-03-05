import java.util.*;

public class AlgoritmAssociation extends Association{

    public AlgoritmAssociation(List<User> user, List<Server> server, Elaboration elaboration){
        this.users = user;
        this.servers = server;
        this.associationMatrix = new int[user.size()][server.size()];
        this.elaboration = elaboration;
        this.totalUnusedBuffer = 0.0;
        this.totalSystemTime = 0.0;
        this.totalEnergy = 0.0;
    }

    public void associationUserServer(List<User> users, List<Server> servers) {
        List<User> unallocatedUsers = new ArrayList<>();
        inizializeAM();

        // User choose the best server for him based on SNR and buffer availability
        for (User user : users) {
            Server bestServer = chooseBestServer(user);
            if (bestServer != null) {
                System.out.println("User choose: " + bestServer + "\n");
                bestServer.getProposedUsers().add(user);
            } else {
                System.out.println("User cannot choose a server");
            }
        }

        // For each server, i'll process the proposed users
        for (Server server : servers) {
            System.out.println("\n\nElaboration in: " + server);
            System.out.println("List of proposed users: " + server.getProposedUsers());

            // Calculate some metrics for evaluation
            for (User proposedUser : server.getProposedUsers()) {
                elaboration.calculateTransmissionTime(proposedUser, server, 0);
                elaboration.calculateComputationTime(proposedUser, server, 0);
                elaboration.calculateLocalComputationTime(proposedUser, server,0);
                elaboration.calculateTransmissionEnergy(proposedUser, server, 0);
                elaboration.calculateComputationEnergy(proposedUser, server, 0);
                elaboration.calculateLocalEnergy(proposedUser, server,0);
                Map<User, Double> ruinDegreeMap = elaboration.associateUserRuinDegree(proposedUser, server);
                System.out.printf("Ruin degree of %s: %.2e%n", proposedUser, ruinDegreeMap.get(proposedUser));
            }

            // Build the priority list using Ruin Theory
            // First the user with less impact on the ruin of server (the one with ruinDegree smaller)
            List<User> priorityList = elaboration.buildPriorityList(server);
            System.out.println("Priority list: " + priorityList);

            // Elaborate every user, if cannot put it into an unallocated list
            for (User user : priorityList) {
                System.out.println("\nElaboration of " + user);
                System.out.printf("Transmission time: %.2e%n", elaboration.getList_value(user, server, elaboration.getTransmissionTime_listAlgoritm()));
                System.out.printf("Transmission energy: %.2e%n", elaboration.getList_value(user, server, elaboration.getTransmissionEnergy_listAlgoritm()));

                totalSystemTime += elaboration.getList_value(user, server, elaboration.getTransmissionTime_listAlgoritm());
                totalEnergy += elaboration.getList_value(user, server, elaboration.getTransmissionEnergy_listAlgoritm());

                if (server.getBuffer() >= user.getTask()) {
                    System.out.println("User can be elaborated");
                    setValueAM(users.indexOf(user), servers.indexOf(server), 1);
                    server.reduceBuffer(user.getTask());

                    System.out.printf("Computation time: %.2e%n", elaboration.getList_value(user, server, elaboration.getComputationTime_listAlgoritm()));
                    System.out.printf("Computation energy: %.2e%n\n", elaboration.getList_value(user, server, elaboration.getComputationEnergy_listAlgoritm()));

                    totalSystemTime += elaboration.getList_value(user, server, elaboration.getComputationTime_listAlgoritm());
                    totalEnergy += elaboration.getList_value(user, server, elaboration.getComputationEnergy_listAlgoritm());

                    System.out.println("Remaining buffer: " + (int) server.getBuffer());

                } else {
                    System.out.println("User cannot be elaborated\n");
                    unallocatedUsers.add(user);
                }
            }
        }

        // Sort the unallocated user from the smallest to biggest with respect ruin theory
        unallocatedUsers = elaboration.sortUnallocatedUsersByTask(unallocatedUsers);
        System.out.println("List of unallocated users: " + unallocatedUsers);

        for (User user : unallocatedUsers) {
            System.out.println("\nElaboration of " + user);
            Server newServer = chooseSecondBestServer(user);
            System.out.println("User choose: " + newServer + "\n");

            if (newServer != null && newServer.getBuffer() >= user.getTask()) {
                System.out.println("User can be elaborated");
                setValueAM(users.indexOf(user), servers.indexOf(newServer), 1);
                newServer.reduceBuffer(user.getTask());

                elaboration.calculateComputationTime(user, newServer, 0);
                elaboration.calculateComputationEnergy(user, newServer, 0);

                System.out.printf("Computation time: %.2e%n", elaboration.getList_value(user, newServer, elaboration.getComputationTime_listAlgoritm()));
                System.out.printf("Computation energy: %.2e%n\n", elaboration.getList_value(user, newServer, elaboration.getComputationEnergy_listAlgoritm()));

                totalSystemTime += elaboration.getList_value(user, newServer, elaboration.getComputationTime_listAlgoritm());
                totalEnergy += elaboration.getList_value(user, newServer, elaboration.getComputationEnergy_listAlgoritm());

            } else if (newServer == null){
                System.out.println("User cannot be elaborated");

                System.out.printf("Local computation time: %.2e%n", elaboration.calculateLocalComputationTimeWithoutServer(user));
                System.out.printf("Local computation energy: %.2e%n\n", elaboration.calculateLocalEnergyWithoutServer(user));

                totalSystemTime += elaboration.calculateLocalComputationTimeWithoutServer(user);
                totalEnergy += elaboration.calculateLocalEnergyWithoutServer(user);

            } else {
                elaboration.calculateLocalComputationTime(user, newServer,0);

                System.out.printf("Local computation time: %.2e%n", elaboration.getList_value(user, newServer, elaboration.getLocalComputationTime_listAlgoritm()));
                System.out.printf("Local computation energy: %.2e%n\n", elaboration.getList_value(user, newServer, elaboration.getLocalEnergy_listAlgoritm()));

                totalSystemTime += elaboration.getList_value(user, newServer, elaboration.getLocalComputationTime_listAlgoritm());
                totalEnergy += elaboration.getList_value(user, newServer, elaboration.getLocalEnergy_listAlgoritm());
            }
        }

        for (Server s : servers) {
            totalUnusedBuffer += s.getBuffer();
            s.setBuffer((int) s.getOriginalBuffer());
        }

        System.out.println("\nTotal unused buffer (cumulative): " + totalUnusedBuffer);
        System.out.printf("Total system time (cumulative): %.2e%n", totalSystemTime);
        System.out.printf("Total energy (cumulative): %.2e%n\n\n", totalEnergy);
    }

    // Function to found the best server based on SNR and buffer availability
    private Server chooseBestServer(User user) {
        Server bestServer = null;
        double bestMetric  = Double.NEGATIVE_INFINITY;

        for (Server server : servers) {
            double snr_value = elaboration.calculateSNR(user, server);
            double bufferAvailability = server.getBuffer() - user.getTask();

            if (bufferAvailability >= 0) {
                double metric = snr_value / (1 + Math.abs(bufferAvailability));
                if (metric > bestMetric) {
                    bestMetric = metric;
                    bestServer = server;
                }
            } else {
                System.out.println("Buffer cannot process task " + user.getTask() + " of " + user);
            }
        }
        return bestServer;
    }

    private Server chooseSecondBestServer(User user) {
        Server bestServer = null;
        double bestMetric  = Double.NEGATIVE_INFINITY;
        List<Match> snr_list = elaboration.getSNR_list();

        for (Server server : servers) {
            double bufferAvailability = server.getBuffer() - user.getTask();

            if (bufferAvailability >= 0) {
                double metric = elaboration.getList_value(user, server, snr_list) / (1 + Math.abs(bufferAvailability));

                if (metric > bestMetric) {
                    bestMetric = metric;
                    bestServer = server;
                }
            }
        }
        return bestServer;
    }

}
