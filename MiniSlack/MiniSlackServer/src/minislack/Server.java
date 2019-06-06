package minislack;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static ServiceInterface slackService;

    public static void main(String[] args) {
        System.out.println("Mini Slack Service start!");
        Registry reg = null;
        try {
            reg = LocateRegistry.createRegistry(2019);
            slackService = new MiniSlackService();
            reg.rebind("MonOD",slackService);
        } catch (RemoteException e){
            e.printStackTrace();
        }
        while(true){}
    }
}
