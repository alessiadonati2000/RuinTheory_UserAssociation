import java.util.*;

public class Main {

    public static void main(String[] args) {
        Elaboration elaboration = new Elaboration();

        User user = new User();
        List<User> users = user.generateUsers(100,900, 10000, 0.2, 0.1);
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

        AlgoritmAssociation algoritmAssociation = new AlgoritmAssociation(users, servers);
        algoritmAssociation.associationUserServer(users, servers, elaboration);

        RandomAssociation randomAssociation = new RandomAssociation(usersRandom, serversRandom);
        randomAssociation.randomAssociation(users, servers, elaboration);

        System.out.println("Associated users with algoritm: " + algoritmAssociation.getTotalNumberAssociatedUsers());
        System.out.println("Associated users with random: " + randomAssociation.getTotalNumberAssociatedUsers());

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

