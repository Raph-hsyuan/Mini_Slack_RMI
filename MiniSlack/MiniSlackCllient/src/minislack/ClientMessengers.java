package minislack;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @Author: HUANG SHENYUAN
 * @Date: 6/7/2019 1:41 AM
 */
public class ClientMessengers extends UnicastRemoteObject implements ClientMessageInterface {

    protected ClientMessengers() throws RemoteException {
        super();
    }

    @Override
    public void displayMsg(String msg) throws RemoteException {
        System.err.print(msg + "\n");
    }
}
