import java.util.*;

public class RandomAssociation extends Association{

    public RandomAssociation(List<User> user, List<Server> server, Elaboration elaboration){
        this.users = user;
        this.servers = server;
        this.associationMatrix = new int[user.size()][server.size()];
        this.elaboration = elaboration;
        this.totalUnusedBuffer = 0.0;
        this.totalSystemTime = 0.0;
        this.totalEnergy = 0.0;
    }

    public void randomAssociation(List<User> users, List<Server> servers) {
        inizializeAM();

        for (User user : users) {
            Server randomServer = chooseRandomServer(user);
            if (randomServer != null) {
                randomServer.getProposedUsers().add(user);
            }
        }

        for (Server server : servers) {
            for (User proposedUser : server.getProposedUsers()) {
                elaboration.calculateTransmissionTime(proposedUser, server, 1);
                elaboration.calculateComputationTime(proposedUser, server, 1);
                elaboration.calculateLocalComputationTime(proposedUser, server,1);
                elaboration.calculateTransmissionEnergy(proposedUser, server, 1);
                elaboration.calculateComputationEnergy(proposedUser, server, 1);
                elaboration.calculateLocalEnergy(proposedUser, server,1);
            }

            for (User user : server.getProposedUsers()) {
                totalSystemTime += elaboration.getList_value(user, server, elaboration.getTransmissionTime_listRandom());
                totalEnergy += elaboration.getList_value(user, server, elaboration.getTransmissionEnergy_listRandom());

                if (server.getBuffer() >= user.getTask()) {
                    setValueAM(users.indexOf(user), servers.indexOf(server), 1);
                    server.reduceBuffer(user.getTask());
                    totalSystemTime += elaboration.getList_value(user, server, elaboration.getComputationTime_listRandom());
                    totalEnergy += elaboration.getList_value(user, server, elaboration.getComputationEnergy_listRandom());

                } else {
                    totalSystemTime += elaboration.getList_value(user, server, elaboration.getLocalComputationTime_listRandom());
                    totalEnergy += elaboration.getList_value(user, server, elaboration.getLocalEnergy_listRandom());
                }
            }
        }

        for (Server s : servers) {
            totalUnusedBuffer += s.getBuffer();
            s.setBuffer((int) s.getOriginalBuffer());
        }
    }

    private Server chooseRandomServer(User user) {
        Random rand = new Random();
        Server randomServer = servers.get(rand.nextInt(servers.size()));
        return randomServer;
    }

}
