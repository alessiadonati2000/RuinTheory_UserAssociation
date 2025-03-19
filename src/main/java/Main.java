import java.util.*;

public class Main {

    public static void main(String[] args) {
        int numSimulations = 500;  // Number of simulation, max value 500, beyond that nothing changes
        int numServer = 3;
        int maxUser = 400;
        int step = 5;
        int[] meanAssociatedUsersAlgoritm = new int[maxUser/step + 1];
        int[] meanAssociatedUsersRandom = new int[maxUser/step + 1];
        int[] meanUnusedResourcesAlgoritm = new int[maxUser/step + 1];
        int[] meanUnusedResourcesRandom = new int[maxUser/step + 1];
        int[] meanTotalSystemTimeAlgoritm = new int[maxUser/step + 1];
        int[] meanTotalSystemTimeRandom = new int[maxUser/step + 1];
        int[] meanTotalEnergyAlgoritm = new int[maxUser/step + 1];
        int[] meanTotalEnergyRandom = new int[maxUser/step + 1];
        int index = 0;

        // I want to run the 500 simulations in a single run, in each varying number of users, thus obtaining the results that will be saved in the previous arrays
        // Im gonna try 5 by 5 -> 5, 10, 15, 20, 25...
        for (int numUsers = 0; numUsers <= maxUser; numUsers += step){
            int sumAssociatedUsersAlgoritm = 0;
            int sumAssociatedUsersRandom = 0;
            double sumUnusedResourcesAlgoritm = 0.0;
            double sumUnusedResourcesRandom = 0.0;
            double sumTotalSystemTimeAlgoritm = 0.0;
            double sumTotalSystemTimeRandom = 0.0;
            double sumTotalEnergyAlgoritm = 0.0;
            double sumTotalEnergyRandom = 0.0;

            // The results will be averaged over a large number of simulations to normalize the data
            for(int i = 0; i < numSimulations; i++) {
                Elaboration elaboration = new Elaboration();

                // Generate users
                User user = new User();
                List<User> users = user.generateUsers(numUsers,900, 10000, 0.2, 0.1);
                List<User> usersRandom = deepCopyUser(users);  // In this way i have a list of User with the same features generated
                for (User u : users){
                    System.out.println(u);
                }  

                // Generate servers
                Server server = new Server();
                List<Server> servers = server.generateServers(numServer, 140000,150000);
                List<Server> serversRandom = deepCopyServer(servers);
                for (Server s : servers) {
                    System.out.println(s);
                }

                System.out.println("\n---------------------ASSOCIATION WITH ALGORITM---------------------\n");
                AlgoritmAssociation algoritmAssociation = new AlgoritmAssociation(users, servers, elaboration);
                algoritmAssociation.associationUserServer(users, servers);
                sumAssociatedUsersAlgoritm += algoritmAssociation.getTotalNumberAssociatedUsers();      // sum of the number of associated users
                sumUnusedResourcesAlgoritm += algoritmAssociation.getTotalUnusedBuffer();               // sum of the total unused buffer
                sumTotalSystemTimeAlgoritm += algoritmAssociation.getTotalSystemTime();                 // sum of the total system time
                sumTotalEnergyAlgoritm += algoritmAssociation.getTotalEnergy();                         // sum of the total energy

                System.out.println("\n----------------------ASSOCIATION WITH RANDOM-----------------------\n");
                RandomAssociation randomAssociation = new RandomAssociation(usersRandom, serversRandom, algoritmAssociation.elaboration);
                randomAssociation.randomAssociation(usersRandom, serversRandom);
                sumAssociatedUsersRandom += randomAssociation.getTotalNumberAssociatedUsers();
                sumUnusedResourcesRandom += randomAssociation.getTotalUnusedBuffer();
                sumTotalSystemTimeRandom += randomAssociation.getTotalSystemTime();
                sumTotalEnergyRandom += randomAssociation.getTotalEnergy();

            } // END SIMULATIONS

            // I find the average number of users associated for all users
            meanAssociatedUsersAlgoritm[index] = sumAssociatedUsersAlgoritm / numSimulations;
            meanAssociatedUsersRandom[index] = sumAssociatedUsersRandom / numSimulations;

            meanUnusedResourcesAlgoritm[index] = (int) (sumUnusedResourcesAlgoritm / numSimulations);
            meanUnusedResourcesRandom[index] = (int) (sumUnusedResourcesRandom / numSimulations);

            meanTotalSystemTimeAlgoritm[index] = (int) sumTotalSystemTimeAlgoritm / numSimulations;
            meanTotalSystemTimeRandom[index] = (int) sumTotalSystemTimeRandom / numSimulations;

            meanTotalEnergyAlgoritm[index] = (int) sumTotalEnergyAlgoritm / numSimulations;
            meanTotalEnergyRandom[index] = (int) sumTotalEnergyRandom / numSimulations;

            index++;

        } // END SIMULATIONS

        System.out.println("Number of associated users");
        System.out.println("Algoritm: " + Arrays.toString(meanAssociatedUsersAlgoritm));
        System.out.println("Random: " + Arrays.toString(meanAssociatedUsersRandom));

        System.out.println("\nNumber of unused resources");
        System.out.println("Algoritm: " + Arrays.toString(meanUnusedResourcesAlgoritm));
        System.out.println("Random: " + Arrays.toString(meanUnusedResourcesRandom));

        System.out.println("\nTotal System Time");
        System.out.println("Algoritm: " + Arrays.toString(meanTotalSystemTimeAlgoritm));
        System.out.println("Random: " + Arrays.toString(meanTotalSystemTimeRandom));

        System.out.println("\nTotal Energy");
        System.out.println("Algoritm: " + Arrays.toString(meanTotalEnergyAlgoritm));
        System.out.println("Random: " + Arrays.toString(meanTotalEnergyRandom));

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

