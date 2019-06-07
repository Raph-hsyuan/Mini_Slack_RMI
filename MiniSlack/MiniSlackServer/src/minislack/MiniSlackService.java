package minislack;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MiniSlackService extends UnicastRemoteObject implements ServiceInterface {

    private static final long serialVersionUID = 1L;
    private Hashtable<String, String> users;
    private Hashtable<String, Boolean> userStates;
    private Hashtable<String, String> groups;
    private Hashtable<String, DurableChat> subscribes;

    MiniSlackService() throws RemoteException {
        groups = new Hashtable<>();
        users = new Hashtable<>();
        userStates = new Hashtable<>();
        subscribes = new Hashtable<>();
        initialGroups();
        initialUsers();
    }

    void initialUsers() {
        users.put("Bob", "123");
        users.put("Alice", "123");
        users.put("Peter", "123");
        userStates.put("Bob", false);
        userStates.put("Alice", false);
        userStates.put("Peter", false);
        DurableChat bob = new DurableChat(groups.get("RMILearningGroup"), "Bob", "");
        DurableChat alice = new DurableChat(groups.get("RMILearningGroup"), "Alice", "");
        DurableChat peter = new DurableChat(groups.get("RMILearningGroup"), "Peter", "");
        bob.joinTopic("RMILearningGroup");
        alice.joinTopic("RMILearningGroup");
        peter.joinTopic("RMILearningGroup");
        alice.joinTopic("PS8");
        peter.joinTopic("PS8");
        subscribes.put("Bob", bob);
        subscribes.put("Alice", alice);
        subscribes.put("Peter", peter);
    }

    void initialGroups() throws RemoteException {
        groups.put("PS8", "tcp://localhost:61616");
        groups.put("RMILearningGroup", "tcp://localhost:61616");
    }

    @Override
    public boolean clientLogin(String clientName, String password) throws RemoteException {
        try {
            if (users.get(clientName).equals(password)) {
                System.out.println(" >" + clientName + " login ----> success");
                this.userStates.replace(clientName, true);
                this.subscribes.get(clientName).start();

                return true;
            } else {
                System.out.println(" >" + clientName + " login ----> failure");
                return false;
            }

        } catch (NullPointerException e) {
            System.out.println(" >" + clientName + " doesn't exist ----> failure");
            return false;
        }
    }

    @Override
    public void clientLogout(String clientName) throws RemoteException {
        this.userStates.replace(clientName, false);
        System.out.println(" >" + clientName + " logout");
    }

    @Override
    public List<String> getAllGroups(String clientName) throws RemoteException {
        if (this.userStates.get(clientName)) {
            List<String> names = new ArrayList<>();
            names.addAll(this.groups.keySet());
            return names;
        } else {
            throw new RuntimeException("[WARN] Login Required");
        }
    }

    @Override
    public List<String> getCurrentGroups(String clientName) throws RemoteException {
        if (this.userStates.get(clientName)) {
            return subscribes.get(clientName).getTopics();
        } else {
            throw new RuntimeException("[WARN] Login Required");
        }
    }

    @Override
    public boolean joinGroup(String clientName, String groupName) throws RemoteException {
        return subscribes.get(clientName).joinTopic(groupName);
    }

    @Override
    public boolean leaveGroup(String clientName, String groupName) throws RemoteException {
        return subscribes.get(clientName).leaveTopic(groupName);
    }

    @Override
    public void bindClientMessage(String username, int port) throws RemoteException {
        subscribes.get(username).bindMessageClient(username, port);

    }

    @Override
    public boolean send(String username, String msg, String topic) {
        return subscribes.get(username).send(msg, topic);
    }
}
