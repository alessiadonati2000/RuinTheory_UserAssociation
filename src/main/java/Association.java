import java.util.*;

public class Association {
    protected List<User> users;
    protected List<Server> servers;
    protected int[][] associationMatrix;
    protected Elaboration elaboration;
    protected double totalUnusedBuffer;
    protected double totalSystemTime;

    public Association() {}

    protected void setValueAM(int userIndex, int serverIndex, int value) {
        associationMatrix[userIndex][serverIndex] = value;
    }

    public int getValueAM(int userIndex, int serverIndex) {
        return associationMatrix[userIndex][serverIndex];
    }

    public void inizializeAM() {
        for (int[] row : associationMatrix) {
            Arrays.fill(row, 0);
        }
    }

    public void printAM() {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println(user);
            for (int j = 0; j < servers.size(); j++) {
                System.out.print(associationMatrix[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public List<User> getAssociatedUsers(int serverIndex) {
        List<User> associated = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            if (getValueAM(i, serverIndex) == 1) {
                associated.add(users.get(i));
            }
        }
        return associated;
    }

    public int getTotalNumberAssociatedUsers() {
        int numAssociatedUsers = 0;
        for (int i = 0; i < users.size(); i++) {
            for (int j = 0; j < servers.size(); j++) {
                if (getValueAM(i, j) == 1) {
                    numAssociatedUsers++;
                }
            }
        }
        return numAssociatedUsers;
    }

    public double getTotalUnusedBuffer() {
        return totalUnusedBuffer;
    }

    public double getTotalSystemTime() {
        return totalSystemTime;
    }
}
