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
    private Hashtable<String, DiscussionGroup> groups;

    MiniSlackService() throws RemoteException {
        groups = new Hashtable<>();
        users = new Hashtable<>();
        userStates = new Hashtable<>();
        initialUsers();
        initialGroups();
    }

    void initialUsers() {
        users.put("Bob", "123");
        users.put("Alice", "123");
        users.put("Peter", "123");
        users.put("Nina", "123");
        users.put("Panda", "123");
        userStates.put("Bob", false);
        userStates.put("Alice", false);
        userStates.put("Peter", false);
        userStates.put("Nina", false);
        userStates.put("Panda", false);
    }

    void initialGroups() throws RemoteException{
        groups.put("PS8", new DiscussionGroup("PS8",1001));
        groups.put("Stage2019", new DiscussionGroup("Stage2019",1002));
    }

    @Override
    public boolean clientLogin(String clientName, String password) throws RemoteException {
        try {
            if (users.get(clientName).equals(password)){
                System.out.println(" >"+clientName+" login ----> success");
                this.userStates.replace(clientName,true);
                return true;
            }
            else{
                System.out.println(" >"+clientName+" login ----> failure");
                return false;
            }

        } catch (NullPointerException e) {
            System.out.println(" >"+clientName+" doesn't exist ----> failure");
            return false;
        }
    }

    @Override
    public void clientLogout(String clientName) throws RemoteException {
        this.userStates.replace(clientName,false);
        System.out.println(" >"+clientName+" logout");
    }

    @Override
    public List<String> getAllGroups(String clientName) throws RemoteException{
        if(this.userStates.get(clientName)){
            List<String> names = new ArrayList<>();
            names.addAll(this.groups.keySet());
            return names;
        }else{
            throw new RuntimeException("[WARN] Login Required");
        }
    }

    @Override
    public List<String> getCurrentGroups(String clientName) throws RemoteException{
        return null;
    }

    @Override
    public boolean joinGroup(String clientName, String groupName) throws RemoteException{
        return false;
    }

    @Override
    public boolean leaveGroup(String clientName, String groupName) throws RemoteException{
        return false;
    }
}
