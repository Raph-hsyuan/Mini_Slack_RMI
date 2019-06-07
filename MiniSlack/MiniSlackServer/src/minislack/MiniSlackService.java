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
    private Hashtable<String, List<DurableChat>> subscribes;

    MiniSlackService() throws RemoteException {
        groups = new Hashtable<>();
        users = new Hashtable<>();
        userStates = new Hashtable<>();
        subscribes = new Hashtable<>();
        initialUsers();
        initialGroups();
    }

    void initialUsers() {
        users.put("Bob", "123");
        users.put("Alice", "123");
        users.put("Peter", "123");
        userStates.put("Bob", false);
        userStates.put("Alice", false);
        userStates.put("Peter", false);
        subscribes.put("Bob", new ArrayList<>());
        subscribes.put("Alice", new ArrayList<>());
        subscribes.put("Peter", new ArrayList<>());
    }

    void initialGroups() throws RemoteException {

        groups.put("PS8", "tcp://localhost:61616");
        groups.put("RMILearningGroup", "tcp://localhost:61616");
        DurableChat bob = new DurableChat(groups.get("RMILearningGroup"), "Bob", "");
        DurableChat alice = new DurableChat(groups.get("RMILearningGroup"), "Alice", "");
        DurableChat peter = new DurableChat(groups.get("RMILearningGroup"), "Peter", "");
        bob.joinTopic("RMILearningGroup");
        alice.joinTopic("RMILearningGroup");
        peter.joinTopic("RMILearningGroup");
        alice.joinTopic("PS8");
        peter.joinTopic("PS8");
        subscribes.get("Bob").add(bob);
        subscribes.get("Alice").add(alice);
        subscribes.get("Peter").add(peter);
    }

    @Override
    public boolean clientLogin(String clientName, String password) throws RemoteException {
        try {
            if (users.get(clientName).equals(password)) {
                System.out.println(" >" + clientName + " login ----> success");
                this.userStates.replace(clientName, true);
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
            return subscribes.get(clientName).get(0).getTopics();
        } else {
            throw new RuntimeException("[WARN] Login Required");
        }
    }

    @Override
    public boolean joinGroup(String clientName, String groupName) throws RemoteException {

        return false;
    }

    @Override
    public boolean leaveGroup(String clientName, String groupName) throws RemoteException {

        return false;
    }

    @Override
    public void bindClientMessage(String username, int port) throws RemoteException {
        for (DurableChat durableChat : subscribes.get(username)) {
            durableChat.bindMessageClient(username, port);
        }
    }

    @Override
    public void send(String username, String msg, String topic) {
        subscribes.get(username).get(0).send(msg, topic);

    }
}
