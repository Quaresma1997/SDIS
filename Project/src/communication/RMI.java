package communication;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI extends Remote {

    // void backup();
    // void restore();
    // void delete();
    // void manageLocalServiceStorage();
    // void retrieveLocalServiceStateInfo();

    String test() throws RemoteException;

}