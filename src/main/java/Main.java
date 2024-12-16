import java.util.*;

public class Main {

    public static void main(String[] args) {
        int numSimulations = 500;
        int maxUser = 300;
        int step = 5;
        int[] meanAssociatedUsersAlgoritm = new int[maxUser/step +1]; //fine-inizio / passo
        int[] meanAssociatedUsersRandom = new int[maxUser/step +1];
        int[] meanUnusedResourcesAlgoritm = new int[maxUser/step +1];
        int[] meanUnusedResourcesRandom = new int[maxUser/step +1];

        int j = 0;

        for (int numUsers = 0; numUsers <= maxUser; numUsers += step){
            int sumAssociatedUsersAlgoritm = 0;
            int sumAssociatedUsersRandom = 0;
            double sumUnusedResourcesAlgoritm = 0.0;
            double sumUnusedResourcesRandom = 0.0;

            for(int i = 0; i < numSimulations; i++) {
                Elaboration elaboration = new Elaboration();

                User user = new User();
                List<User> users = user.generateUsers(numUsers,900, 10000, 0.2, 0.1);
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
                sumUnusedResourcesAlgoritm += algoritmAssociation.getTotalUnusedBuffer();

                System.out.println("----------------------ASSOCIATION WITH RANDOM-----------------------\n");
                RandomAssociation randomAssociation = new RandomAssociation(usersRandom, serversRandom, algoritmAssociation.elaboration);
                randomAssociation.randomAssociation(usersRandom, serversRandom);
                sumUnusedResourcesRandom += randomAssociation.getTotalUnusedBuffer();

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

            meanAssociatedUsersAlgoritm[j] = sumAssociatedUsersAlgoritm / numSimulations;
            meanAssociatedUsersRandom[j] = sumAssociatedUsersRandom / numSimulations;
            meanUnusedResourcesAlgoritm[j] = (int) (sumUnusedResourcesAlgoritm / numSimulations);
            meanUnusedResourcesRandom[j] = (int) (sumUnusedResourcesRandom / numSimulations);
            j++;
        }

        // OK: l'algoritmo associa più utenti rispetto al randomico
        System.out.println("Number of associated users");
        System.out.println("Algoritm: " + Arrays.toString(meanAssociatedUsersAlgoritm));
        System.out.println("Random: " + Arrays.toString(meanAssociatedUsersRandom));

        // QUASI: in generale l'algoritmo alloca più risorse del randomico, tranne tra 80 e 130 dove il randomico ottimizza meglio le risorse
        // questo perchè magari il randomico alloca utenti più con task più grande subito, mentre con l'algoritmo vengono lasciati alla fine
        System.out.println("Number of unused resources");
        System.out.println("Algoritm: " + Arrays.toString(meanUnusedResourcesAlgoritm));
        System.out.println("Random: " + Arrays.toString(meanUnusedResourcesRandom));

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

