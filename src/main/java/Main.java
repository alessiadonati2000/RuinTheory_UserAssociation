import java.util.*;

public class Main {

    public static void main(String[] args) {
        Elaboration elaboration = new Elaboration();

        User user = new User();
        List<User> users = user.generateUsers(3,900, 10000, 0.2, 0.1);
        List<User> usersRandom = deepCopyUser(users);
        List<User> usersTime = deepCopyUser(users);
        for (User u : users){
            System.out.println(u);
        }

        Server server = new Server();
        List<Server> servers = server.generateServers(3, 140000,150000);
        List<Server> serversRandom = deepCopyServer(servers);
        List<Server> serversTime = deepCopyServer(servers);
        for (Server s : servers) {
            System.out.println(s);
        }

        System.out.println("---------------ASSOCIATION WITH ALGORITM---------------");
        AlgoritmAssociation algoritmAssociation = new AlgoritmAssociation(users, servers, elaboration);
        algoritmAssociation.associationUserServer(users, servers);

        System.out.println("----------------ASSOCIATION WITH RANDOM-----------------");
        RandomAssociation randomAssociation = new RandomAssociation(usersRandom, serversRandom, elaboration);
        randomAssociation.randomAssociation(usersRandom, serversRandom);

        /*System.out.println("-----------------ASSOCIATION WITH TIME-----------------");
        TimeAssociation timeAssociation = new TimeAssociation(usersTime, serversTime, elaboration);
        timeAssociation.associationTime(usersTime, serversTime);*/

        System.out.println("Associated users with algoritm: " + algoritmAssociation.getTotalNumberAssociatedUsers());
        System.out.println("Associated users with random: " + randomAssociation.getTotalNumberAssociatedUsers());
        //System.out.println("Associated users with time algoritm: " + timeAssociation.getTotalNumberAssociatedUsers());

        // TODO: calcola i tempi di calcolo e trasmissione
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

