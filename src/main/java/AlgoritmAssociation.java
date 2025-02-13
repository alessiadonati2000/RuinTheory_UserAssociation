import java.util.*;

public class AlgoritmAssociation extends Association{

    public AlgoritmAssociation(List<User> user, List<Server> server, Elaboration elaboration){
        this.users = user;
        this.servers = server;
        this.associationMatrix = new int[user.size()][server.size()];
        this.elaboration = elaboration;
        this.totalUnusedBuffer = 0.0;
        this.totalSystemTime = 0.0;
    }

    public void associationUserServer(List<User> users, List<Server> servers) {
        List<User> unallocatedUsers = new ArrayList<>();
        inizializeAM();

        System.out.println("----------------USER PROPOSED TO BEST SERVER FOR HIM---------------\n");
        for (User user : users) {
            Server bestServer = chooseBestServer(user);
            if (bestServer != null) {
                bestServer.getProposedUsers().add(user);
            }
        }

        for (Server server : servers) {
            System.out.println("---------------ELABORATION IN " + server + "\n");

            // Calcolo per gli utenti proposti i vari parametri
            for (User proposedUser : server.getProposedUsers()) {
                elaboration.calculateTransmissionTime(proposedUser, server, 0);
                elaboration.calculateComputationTime(proposedUser, server, 0);
                elaboration.calculateLocalComputationTime(proposedUser, server,0);
                elaboration.associateUserRuinDegree(proposedUser, server);
            }

            // Costruisco la lista di priorità
            List<User> priorityList = elaboration.buildPriorityList(server);

            System.out.println("START - ASSOCIATION TASK-BUFFER");
            // Se il server ha buffer disponibile serve l'utente, altrimenti viene inserito in una lista per il ricalcolo
            for (User user : priorityList) {
                totalSystemTime += elaboration.getList_value(user, server, elaboration.getTransmissionTime_listAlgoritm());

                if (server.getBuffer() >= user.getTask()) {
                    setValueAM(users.indexOf(user), servers.indexOf(server), 1);
                    server.reduceBuffer(user.getTask());
                    totalSystemTime += elaboration.getList_value(user, server, elaboration.getComputationTime_listAlgoritm());

                } else {
                    unallocatedUsers.add(user);
                }
            }

            System.out.println("END - ASSOCIATION TASK-BUFFER\n");
        }

        // Tentativo di riallocazione per gli utenti rifiutati
        System.out.println("\n--------START - REALLOCATION PROCESS FOR UNALLOCATED USERS\n");

        for (User user : unallocatedUsers) {
            Server newServer = chooseSecondBestServer(user);

            if (newServer != null && newServer.getBuffer() >= user.getTask()) {
                setValueAM(users.indexOf(user), servers.indexOf(newServer), 1);
                newServer.reduceBuffer(user.getTask());

                elaboration.calculateTransmissionTime(user, newServer, 0);
                totalSystemTime += elaboration.getList_value(user, newServer, elaboration.getTransmissionTime_listAlgoritm());

                elaboration.calculateComputationTime(user, newServer, 0);
                totalSystemTime += elaboration.getList_value(user, newServer, elaboration.getComputationTime_listAlgoritm());

                System.out.println(user + " reassigned to " + newServer);

            } else if (newServer == null){
                System.out.println("FAILED to reallocate " + user + ". Task will be computed locally.");

            } else {
                // TODO trovare un modo per andare a sommare il tempo di calcolo locale anche di quelli che non trovano un server, magari creo una funzione solo per loro
                elaboration.calculateTransmissionTime(user, newServer, 0);
                totalSystemTime += elaboration.getList_value(user, newServer, elaboration.getTransmissionTime_listAlgoritm());
                elaboration.calculateLocalComputationTime(user, newServer,0);
                totalSystemTime += elaboration.getList_value(user, newServer, elaboration.getLocalComputationTime_listAlgoritm());
                System.out.println("FAILED to reallocate " + user + ". Task will be computed locally.");
            }
        }

        System.out.println("\n--------------------REALLOCATION PROCESS ENDED\n");

        // Reset dei server e calcolo dello spazio inutilizzato
        for (Server s : servers) {
            totalUnusedBuffer += s.getBuffer();
            s.setBuffer((int) s.getOriginalBuffer());
        }
    }

    private Server chooseBestServer(User user) {
        // Scelgo il miglior server in base al rapposto segnale-rumore e al suo buffer disponibile
        Server bestServer = null;
        double bestMetric  = Double.NEGATIVE_INFINITY;

        for (Server server : servers) {
            double snr_value = elaboration.calculateSNR(user, server);
            double bufferAvailability = server.getBuffer() - user.getTask();
            System.out.println(user + " " + server + " SNR: " + (int) snr_value);

            if (bufferAvailability >= 0) {
                double metric = snr_value / (1 + Math.abs(bufferAvailability)); // Penalizzo server con poco buffer e privilegio quello con snr più alto

                if (metric > bestMetric) {
                    bestMetric = metric;
                    bestServer = server;
                }
            }
        }

        System.out.println("User choose: " + bestServer + "\n");
        return bestServer;
    }

    private Server chooseSecondBestServer(User user) {
        Server bestServer = null;
        double bestMetric  = Double.NEGATIVE_INFINITY;
        List<Match> snr_list = elaboration.getSNR_list();

        for (Server server : servers) {
            double bufferAvailability = server.getBuffer() - user.getTask();

            if (bufferAvailability >= 0) {
                double metric = elaboration.getList_value(user, server, snr_list) / (1 + Math.abs(bufferAvailability)); // Penalizzo server con poco buffer e privilegio quello con snr più alto

                if (metric > bestMetric) {
                    bestMetric = metric;
                    bestServer = server;
                }
            }
        }

        System.out.println("User choose: " + bestServer + "\n");
        return bestServer;
    }


}
