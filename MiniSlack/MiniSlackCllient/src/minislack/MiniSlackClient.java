package minislack;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiniSlackClient{
    private Logger log = Logger.getLogger("MiniSlackClient");
    public String clientName = null;
    private ServiceInterface service = null;
    public boolean online = false;
    private ClientMessageInterface messageInterface = null;
    MiniSlackClient() throws RemoteException{
        try {
            Registry reg = LocateRegistry.getRegistry("192.168.56.1", 2019);
            service = (ServiceInterface) reg.lookup("MonOD");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    boolean login(String userName, String password){
        try{
            if(service.clientLogin(userName, password)){
                log.log(Level.INFO,userName + " --> Login Successfully");
                clientName = userName;
                online = true;
                System.out.println("Mini Slack Service start!");
                Registry reg = null;
                int port = (int)(Math.random() * 3000);
                // publish message dispservice
                try {
                    reg = LocateRegistry.createRegistry(port);
                    messageInterface = new ClientMessengers();
                    reg.rebind(userName,messageInterface);
                } catch (RemoteException e){
                    e.printStackTrace();
                }
                service.bindClientMessage(userName,port);
                return true;
            }else{
                log.log(Level.INFO,"Wrong username or password --> Login Failure");
                return false;
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
        log.log(Level.WARNING,"Connection failure");
        return false;
    }

    void logout(){
        if(clientName!=null){
            try {
                service.clientLogout(clientName);
                log.log(Level.INFO,clientName + " --> Logout Successfully");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            log.log(Level.WARNING,"You are offline");
        }

    }

    boolean isOnline(){
        return online;
    }

    String getAllGroups(){
        try {
            StringBuilder builder = new StringBuilder();
            for(String s:service.getAllGroups(clientName)){
                builder.append("#"+s+"\n");
            }
            return builder.toString();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    String getMyGroups(){
        try {
            StringBuilder builder = new StringBuilder();
            for(String s:service.getCurrentGroups(clientName)){
                builder.append("#"+s+"\n");
            }
            return builder.toString();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    void send(String msg, String topic){
        try {
            service.send(clientName,msg,topic);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
