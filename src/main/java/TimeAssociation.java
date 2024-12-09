import java.util.*;

public class TimeAssociation extends Association{
    private List<Match> transmissionTime;

    public TimeAssociation(List<User> user, List<Server> server){
        this.users = user;
        this.servers = server;
        this.associationMatrix = new int[user.size()][server.size()];
        this.elaboration = new Elaboration();
    }

    public void associationTime(List<User> users, List<Server> servers, Elaboration elaboration, AlgoritmAssociation algoritmAssociation) {
        inizializeAM();

        System.out.println("//////////////////////////////////////////////////////////////////\n");
        System.out.println("USER PROPOSED TO BEST SERVER FOR HIM\n");
        for (User user : users) {
            Server bestServer = chooseBestServerTime(user, algoritmAssociation);
            if (bestServer != null) {
                bestServer.getPropostedUsers().add(user);
            }
        }
        System.out.println("//////////////////////////////////////////////////////////////////\n");

        for (Server server : servers) {
            System.out.println("ELABORATION IN " + server + "\n");
            System.out.println("List of proposed users:\n" + server.getPropostedUsers() + "\n");

            System.out.println("-------------START WITH COMPUTATION TIME-------------");
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
                }
            }
            System.out.print("//////////////////////////////////////////////////////////////////\n");
            System.out.println();
        }
    }

    private Server chooseBestServerTime(User user, AlgoritmAssociation algoritmAssociation) {
        Server bestServer = null;
        double bestTransmissionTime = Double.POSITIVE_INFINITY;

        for (Server server : servers) {
            transmissionTime = elaboration.calculateTransmissionTime(user, server, algoritmAssociation.getListSNR());
            double transmissionTime_value = elaboration.getTransmissionTime(user, server, transmissionTime);
            System.out.println(user + " " + server + " Trans. time: " + (int) transmissionTime_value);
            if (transmissionTime_value < bestTransmissionTime) {
                bestTransmissionTime = transmissionTime_value;
                bestServer = server;
            }
        }

        System.out.println("User choose: " + bestServer);
        System.out.println();
        return bestServer;
    }



}
