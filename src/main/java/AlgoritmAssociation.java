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
        for (User user : users) {
            Server bestServer = chooseBestServer(user);
            if (bestServer != null) {
                bestServer.getProposedUsers().add(user);
            }
        }
        System.out.println("//////////////////////////////////////////////////////////////////\n");

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
                System.out.println(user);
                totalSystemTime += elaboration.getList_value(user, server, elaboration.getTransmissionTime_listAlgoritm());

                if (server.getBuffer() >= user.getTask()) {
                    setValueAM(users.indexOf(user), servers.indexOf(server), 1);
                    server.reduceBuffer(user.getTask());
                    totalSystemTime += elaboration.getList_value(user, server, elaboration.getComputationTime_listAlgoritm());

                    System.out.println("Update buffer: " + (int) server.getBuffer() + "\n");

                } else {
                    totalSystemTime += elaboration.getList_value(user, server, elaboration.getLocalComputationTime_listAlgoritm());
                    System.out.println("ERROR: BUFFER EXAUSTED");
                }
            }

            totalUnusedBuffer += server.getBuffer();
            System.out.println("buffer non utilizzato: " + (int) totalUnusedBuffer);

            System.out.print("//////////////////////////////////////////////////////////////////\n");
            System.out.println();
        }

        // Reset dei server
        for (Server s : servers) {
            s.setBuffer((int) s.getOriginalBuffer());
        }
    }

    private Server chooseBestServer(User user) {
        Server bestServer = null;
        double bestSNR = Double.NEGATIVE_INFINITY;

        for (Server server : servers) {
            double snr_value = elaboration.calculateSNR(user, server);
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


}
