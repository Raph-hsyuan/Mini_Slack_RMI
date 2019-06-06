package minislack;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiniSlackClient {
    private Logger log = Logger.getLogger("MiniSlackClient");
    public String clientName = null;
    ServiceInterface service = null;
    public boolean online = false;


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

}
