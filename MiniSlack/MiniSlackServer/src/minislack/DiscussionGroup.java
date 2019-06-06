package minislack;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class DiscussionGroup extends UnicastRemoteObject {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<String> subscribers;

    DiscussionGroup(String name,int numport)throws RemoteException {
        super(numport);
        this.name = name;
        subscribers = new ArrayList<>();
    }

    List<String> getSubscribers() throws RemoteException{
        List<String> copy = new ArrayList<>();
        copy.addAll(subscribers);
        return copy;
    }

    void subscribe(String name) throws RemoteException{
        this.subscribers.add(name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
