import java.util.*;

public class Main {

    public static void main(String[] args) {
        int numSimulations = 500;
        int sumAssociatedUsersAlgoritm = 0;
        int sumAssociatedUsersRandom = 0;
        int meanAssociatedUsersAlgortim = 0;
        int meanAssociatedUsersRandom = 0;

        for(int i = 0; i < numSimulations; i++) {
            Elaboration elaboration = new Elaboration();

            User user = new User();
            List<User> users = user.generateUsers(25,900, 10000, 0.2, 0.1);
            List<User> usersRandom = deepCopyUser(users);
            for (User u : users){
                System.out.println(u);
            }

            Server server = new Server();
            List<Server> servers = server.generateServers(3, 140000,150000);
            List<Server> serversRandom = deepCopyServer(servers);
            for (Server s : servers) {
                System.out.println(s);
            }

            System.out.println("---------------------ASSOCIATION WITH ALGORITM---------------------\n");
            AlgoritmAssociation algoritmAssociation = new AlgoritmAssociation(users, servers, elaboration);
            algoritmAssociation.associationUserServer(users, servers);

            System.out.println("----------------------ASSOCIATION WITH RANDOM-----------------------\n");
            RandomAssociation randomAssociation = new RandomAssociation(usersRandom, serversRandom, algoritmAssociation.elaboration);
            randomAssociation.randomAssociation(usersRandom, serversRandom);

            double meanTransmissionTime = 0.0;
            double meanComputationTime = 0.0;
            double meanTotalTime = 0.0;

            System.out.println("Associated users with algoritm: " + algoritmAssociation.getTotalNumberAssociatedUsers());
            sumAssociatedUsersAlgoritm += algoritmAssociation.getTotalNumberAssociatedUsers();
            /*for(Server s : servers){
                meanTransmissionTime = elaboration.calculateMeanTransmissionTime(s,0);
                meanComputationTime = elaboration.calculateMeanComputationTime(s,0);
                System.out.println("Mean Transmission Time of server" + s + ": " + meanTransmissionTime);
                System.out.println("Mean Computation Time of server" + s + ": " + meanComputationTime);
            }*/

            System.out.println("Associated users with random: " + randomAssociation.getTotalNumberAssociatedUsers());
            sumAssociatedUsersRandom += randomAssociation.getTotalNumberAssociatedUsers();
            /*for(Server s : serversRandom){
                meanTransmissionTime = elaboration.calculateMeanTransmissionTime(s,1);
                meanComputationTime = elaboration.calculateMeanComputationTime(s,1);
                System.out.println("Mean Transmission Time of " + s + ": " + meanTransmissionTime);
                System.out.println("Mean Computation Time of server" + s + ": " + meanComputationTime);
            }*/
        }

        System.out.println("Mean of associated users with algoritm with 500 simulations: " + (sumAssociatedUsersAlgoritm / 500));
        System.out.println("Mean of associated users with random with 500 simulations: " + (sumAssociatedUsersRandom / 500));

    }

    private static List<Server> deepCopyServer(List<Server> originalList) {
        List<Server> copiedList = new ArrayList<>();
        for (Server item : originalList) {
            copiedList.add(new Server(item.getBuffer()));
        }
        return copiedList;
    }
    private static List<User> deepCopyUser(List<User> originalList) {
        List<User> copiedList = new ArrayList<>();
        for (User item : originalList) {
            copiedList.add(new User(item.getTask(), item.getTransmissionPower()));
        }
        return copiedList;
    }
}

