import java.util.*;

public class RandomAssociation extends Association{
    private double totalUnusedBuffer;

    public RandomAssociation(List<User> user, List<Server> server, Elaboration elaboration){
        this.users = user;
        this.servers = server;
        this.associationMatrix = new int[user.size()][server.size()];
        this.elaboration = elaboration;
        this.totalUnusedBuffer = 0.0;
    }

    public void randomAssociation(List<User> users, List<Server> servers) {
        inizializeAM();

        System.out.println("//////////////////////////////////////////////////////////////////\n");
        System.out.println("USER PROPOSED TO BEST SERVER FOR HIM\n");
        for (User user : users) {
            Server randomServer = chooseRandomServer(user);
            if (randomServer != null) {
                randomServer.getProposedUsers().add(user);
            }
        }
        System.out.print("//////////////////////////////////////////////////////////////////\n\n");

        for (Server server : servers) {
            System.out.println("ELABORATION IN " + server + "\n");
            System.out.println("List of proposed users:\n" + server.getProposedUsers() + "\n");

            System.out.println("----------------------------CALCULATE TRANSMISSION TIME----------------------------");
            for (User proposedUser : server.getProposedUsers()) {
                double transmissionTime_value = elaboration.calculateTransmissionTime(proposedUser, server, 1);
                System.out.println(proposedUser + " " + server + " Transmition time: " + transmissionTime_value);
            }
            System.out.println("-----------------------------------------END---------------------------------------\n");

            System.out.println("----------------------------CALCULATE COMPUTING TIME----------------------------");
            for (User proposedUser : server.getProposedUsers()) {
                double computationTime_value = elaboration.calculateComputationTime(proposedUser, server, 1);
                System.out.println(proposedUser + " " + server + " Computation time: " + computationTime_value);
            }
            System.out.println("-----------------------------------------END---------------------------------------\n");

            System.out.println("START ASSOCIATION TASK-BUFFER");
            for (User user : server.getProposedUsers()) {
                System.out.println(user);

                if (server.getBuffer() >= user.getTask()) {
                    setValueAM(users.indexOf(user), servers.indexOf(server), 1);
                    server.reduceBuffer(user.getTask());

                    System.out.println("Update buffer: " + (int) server.getBuffer() + "\n");

                } else {
                    System.out.println("ERROR: BUFFER EXAUSTED");
                    break;
                }
            }
            System.out.print("//////////////////////////////////////////////////////////////////\n");
            System.out.println();

            totalUnusedBuffer += server.getBuffer();
        }

        // Reset dei server
        for (Server s : servers) {
            s.setBuffer((int) s.getOriginalBuffer());
        }
    }

    private Server chooseRandomServer(User user) {
        Random rand = new Random();
        Server randomServer = servers.get(rand.nextInt(servers.size()));
        System.out.println("User " + user + "choose: " + randomServer);
        return randomServer;
    }

    public double getTotalUnusedBuffer() {
        return totalUnusedBuffer;
    }
}
