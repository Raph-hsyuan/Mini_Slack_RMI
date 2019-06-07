package minislack;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServiceInterface extends Remote {
    boolean clientLogin(String clientName, String password) throws RemoteException;

    void clientLogout(String clientName) throws RemoteException;

    List<String> getAllGroups(String clientName) throws RemoteException;

    List<String> getCurrentGroups(String clientName) throws RemoteException;

    boolean joinGroup(String clientName, String groupName) throws RemoteException;

    boolean leaveGroup(String clientName, String groupName) throws RemoteException;

    void bindClientMessage(String userName, int port) throws RemoteException;

    public boolean send(String username, String msg, String topic) throws RemoteException;
}
