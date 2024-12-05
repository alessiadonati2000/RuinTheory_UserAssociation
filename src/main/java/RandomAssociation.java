import java.util.*;

public class RandomAssociation extends Association{

    public RandomAssociation(List<User> user, List<Server> server){
        this.users = user;
        this.servers = server;
        this.associationMatrix = new int[user.size()][server.size()];
        this.elaboration = new Elaboration();
    }

    public void randomAssociation(List<User> users, List<Server> servers, Elaboration elaboration) {
        inizializeAM();

        System.out.println("//////////////////////////////////////////////////////////////////\n");
        System.out.println("USER PROPOSED TO BEST SERVER FOR HIM\n");
        for (User user : users) {
            Server randomServer = chooseRandomServer(user);
            if (randomServer != null) {
                randomServer.getPropostedUsers().add(user);
            }
        }
        System.out.print("//////////////////////////////////////////////////////////////////\n\n");

        for (Server server : servers) {
            System.out.println("ELABORATION IN " + server + "\n");
            System.out.println("List of proposed users:\n" + server.getPropostedUsers() + "\n");

            System.out.println("START ASSOCIATION TASK-BUFFER");
            for (User user : server.getPropostedUsers()) {
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

    private Server chooseRandomServer(User user) {
        Random rand = new Random();
        Server randomServer = servers.get(rand.nextInt(servers.size()));
        System.out.println("User choose: " + randomServer);
        return randomServer;
    }
}
