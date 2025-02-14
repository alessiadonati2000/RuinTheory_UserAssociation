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

        for (User user : users) {
            Server bestServer = chooseBestServer(user);
            if (bestServer != null) {
                bestServer.getProposedUsers().add(user);
            }
        }

        for (Server server : servers) {
            for (User proposedUser : server.getProposedUsers()) {
                elaboration.calculateTransmissionTime(proposedUser, server, 0);
                elaboration.calculateComputationTime(proposedUser, server, 0);
                elaboration.calculateLocalComputationTime(proposedUser, server,0);
                elaboration.calculateTransmissionEnergy(proposedUser, server, 0);
                elaboration.calculateComputationEnergy(proposedUser, server, 0);
                elaboration.calculateLocalEnergy(proposedUser, server,0);
            }

            List<User> priorityList = elaboration.buildPriorityList(server);
            System.out.println("Lista di priorità: " + priorityList + "\n\n");

            for (User user : priorityList) {
                totalSystemTime += elaboration.getList_value(user, server, elaboration.getTransmissionTime_listAlgoritm());
                totalEnergy += elaboration.getList_value(user, server, elaboration.getTransmissionEnergy_listAlgoritm());

                if (server.getBuffer() >= user.getTask()) {
                    setValueAM(users.indexOf(user), servers.indexOf(server), 1);
                    server.reduceBuffer(user.getTask());
                    totalSystemTime += elaboration.getList_value(user, server, elaboration.getComputationTime_listAlgoritm());
                    totalEnergy += elaboration.getList_value(user, server, elaboration.getComputationEnergy_listAlgoritm());

                    System.out.println("Buffer rimanente: " + (int) server.getBuffer());

                } else {
                    unallocatedUsers.add(user);
                }
            }
        }

        unallocatedUsers = elaboration.sortUnallocatedUsersByTask(unallocatedUsers);
        System.out.println("Lista di priorità: " + unallocatedUsers + "\n\n");

        for (User user : unallocatedUsers) {
            Server newServer = chooseSecondBestServer(user);

            if (newServer != null && newServer.getBuffer() >= user.getTask()) {
                setValueAM(users.indexOf(user), servers.indexOf(newServer), 1);
                newServer.reduceBuffer(user.getTask());
                elaboration.calculateComputationTime(user, newServer, 0);
                elaboration.calculateComputationEnergy(user, newServer, 0);

                totalSystemTime += elaboration.getList_value(user, newServer, elaboration.getComputationTime_listAlgoritm());
                totalEnergy += elaboration.getList_value(user, newServer, elaboration.getComputationEnergy_listAlgoritm());

            } else if (newServer == null){
                totalSystemTime += elaboration.calculateLocalComputationTimeWithoutServer(user);
                totalEnergy += elaboration.calculateLocalEnergyWithoutServer(user);

            } else {
                elaboration.calculateLocalComputationTime(user, newServer,0);
                totalSystemTime += elaboration.getList_value(user, newServer, elaboration.getLocalComputationTime_listAlgoritm());
                totalEnergy += elaboration.getList_value(user, newServer, elaboration.getLocalEnergy_listAlgoritm());

            }
        }

        for (Server s : servers) {
            totalUnusedBuffer += s.getBuffer();
            s.setBuffer((int) s.getOriginalBuffer());
        }
    }

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
