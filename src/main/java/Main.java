import java.util.*;

public class Main {

    public static void main(String[] args) {
        int numSimulations = 500;
        int numServer = 3;
        int maxUser = 300;
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

        // Voglio simulare in una unica run i vari risultati al variare del numero di utenti
        // andrò a provare di 5 in 5 -> 5, 10, 15, 20, 25...
        for (int numUsers = 0; numUsers <= maxUser; numUsers += step){
            int sumAssociatedUsersAlgoritm = 0;
            int sumAssociatedUsersRandom = 0;
            double sumUnusedResourcesAlgoritm = 0.0;
            double sumUnusedResourcesRandom = 0.0;
            double sumTotalSystemTimeAlgoritm = 0.0;
            double sumTotalSystemTimeRandom = 0.0;
            double sumTotalEnergyAlgoritm = 0.0;
            double sumTotalEnergyRandom = 0.0;

            // I risultati saranno la media su un elevato numero di simulazioni per normalizzare il dato
            for(int i = 0; i < numSimulations; i++) {
                Elaboration elaboration = new Elaboration();

                // Generate users
                User user = new User();
                List<User> users = user.generateUsers(numUsers,900, 10000, 0.2, 0.1);
                List<User> usersRandom = deepCopyUser(users);
                /*for (User u : users){
                    System.out.println(u);
                }*/

                // Generate servers
                Server server = new Server();
                List<Server> servers = server.generateServers(numServer, 140000,150000);
                List<Server> serversRandom = deepCopyServer(servers);
                /*for (Server s : servers) {
                    //System.out.println(s);
                }*/

                //System.out.println("\n---------------------ASSOCIATION WITH ALGORITM---------------------\n");
                AlgoritmAssociation algoritmAssociation = new AlgoritmAssociation(users, servers, elaboration);
                algoritmAssociation.associationUserServer(users, servers);
                sumAssociatedUsersAlgoritm += algoritmAssociation.getTotalNumberAssociatedUsers();      // sum the number ho associated users
                sumUnusedResourcesAlgoritm += algoritmAssociation.getTotalUnusedBuffer();               // sum the total unused buffer
                sumTotalSystemTimeAlgoritm += algoritmAssociation.getTotalSystemTime();
                sumTotalEnergyAlgoritm += algoritmAssociation.getTotalEnergy();

                //algoritmAssociation.printAM();

                //System.out.println("\n----------------------ASSOCIATION WITH RANDOM-----------------------\n");
                RandomAssociation randomAssociation = new RandomAssociation(usersRandom, serversRandom, algoritmAssociation.elaboration);
                randomAssociation.randomAssociation(usersRandom, serversRandom);
                sumAssociatedUsersRandom += randomAssociation.getTotalNumberAssociatedUsers();
                sumUnusedResourcesRandom += randomAssociation.getTotalUnusedBuffer();
                sumTotalSystemTimeRandom += randomAssociation.getTotalSystemTime();
                sumTotalEnergyRandom += randomAssociation.getTotalEnergy();
                //randomAssociation.printAM();

            } // FINE SIMULAZIONI

            // Trovo il numero medio di utenti associati per tot utenti
            meanAssociatedUsersAlgoritm[index] = sumAssociatedUsersAlgoritm / numSimulations;
            meanAssociatedUsersRandom[index] = sumAssociatedUsersRandom / numSimulations;

            // Trovo le risorse non utilizzate per tot utenti
            meanUnusedResourcesAlgoritm[index] = (int) (sumUnusedResourcesAlgoritm / numSimulations);
            meanUnusedResourcesRandom[index] = (int) (sumUnusedResourcesRandom / numSimulations);

            meanTotalSystemTimeAlgoritm[index] = (int) sumTotalSystemTimeAlgoritm / numSimulations;
            meanTotalSystemTimeRandom[index] = (int) sumTotalSystemTimeRandom / numSimulations;

            meanTotalEnergyAlgoritm[index] = (int) sumTotalEnergyAlgoritm / numSimulations;
            meanTotalEnergyRandom[index] = (int) sumTotalEnergyRandom / numSimulations;

            index++;

        } // FINE CALCOLO CON TOT NUMUSER

        // OK: l'algoritmo associa più utenti rispetto al randomico
        System.out.println("Number of associated users");
        System.out.println("Algoritm: " + Arrays.toString(meanAssociatedUsersAlgoritm));
        System.out.println("Random: " + Arrays.toString(meanAssociatedUsersRandom));

        // QUASI: in generale l'algoritmo alloca più risorse del randomico, tranne tra 80 e 130 dove il randomico ottimizza meglio le risorse
        // questo perchè magari il randomico alloca utenti più con task più grande subito, mentre con l'algoritmo vengono lasciati alla fine
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

