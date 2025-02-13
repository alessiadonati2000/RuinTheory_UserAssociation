import java.util.*;

public class RandomAssociation extends Association{

    public RandomAssociation(List<User> user, List<Server> server, Elaboration elaboration){
        this.users = user;
        this.servers = server;
        this.associationMatrix = new int[user.size()][server.size()];
        this.elaboration = elaboration;
        this.totalUnusedBuffer = 0.0;
        this.totalSystemTime = 0.0;
    }

    public void randomAssociation(List<User> users, List<Server> servers) {
        inizializeAM();

        System.out.println("------------------USER PROPOSED TO A SERVER FOR HIM-----------------\n");
        for (User user : users) {
            Server randomServer = chooseRandomServer(user);
            if (randomServer != null) {
                randomServer.getProposedUsers().add(user);
            }
        }

        for (Server server : servers) {
            System.out.println("\n------------------ELABORATION IN " + server + "\n");

            for (User proposedUser : server.getProposedUsers()) {
                elaboration.calculateTransmissionTime(proposedUser, server, 1);
                elaboration.calculateComputationTime(proposedUser, server, 1);
                elaboration.calculateLocalComputationTime(proposedUser, server,1);
            }

            System.out.println("START - ASSOCIATION TASK-BUFFER");
            for (User user : server.getProposedUsers()) {
                totalSystemTime += elaboration.getList_value(user, server, elaboration.getTransmissionTime_listRandom());

                if (server.getBuffer() >= user.getTask()) {
                    setValueAM(users.indexOf(user), servers.indexOf(server), 1);
                    server.reduceBuffer(user.getTask());
                    totalSystemTime += elaboration.getList_value(user, server, elaboration.getComputationTime_listRandom());

                } else {
                    totalSystemTime += elaboration.getList_value(user, server, elaboration.getLocalComputationTime_listRandom());
                }
            }
            System.out.println("END - ASSOCIATION TASK-BUFFER\n");
        }

        // Reset dei server
        for (Server s : servers) {
            // TODO: capire perchè il randomico ha risultati migliori sul buffer inutilizzato
            totalUnusedBuffer += s.getBuffer();
            s.setBuffer((int) s.getOriginalBuffer());
        }
    }

    private Server chooseRandomServer(User user) {
        Random rand = new Random();
        Server randomServer = servers.get(rand.nextInt(servers.size()));
        System.out.println(user + " choose: " + randomServer);
        return randomServer;
    }

}
