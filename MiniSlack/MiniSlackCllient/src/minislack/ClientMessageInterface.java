package minislack;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientMessageInterface extends Remote {
    void displayMsg(String msg) throws RemoteException;
}
