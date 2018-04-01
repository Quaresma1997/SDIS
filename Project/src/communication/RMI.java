package communication;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

public interface RMI extends Remote {

    // void backup();
    // void restore();
    // void delete();
    // void manageLocalServiceStorage();
    // void retrieveLocalServiceStateInfo();

    void backup(String filepath, int repDeg, byte[] fileData) throws IOException, NoSuchAlgorithmException, InterruptedException;
    void restore(String filepath) throws IOException, InterruptedException;
    void delete(String filepath) throws IOException, NoSuchAlgorithmException, InterruptedException;
    void reclaimDiskSpace(long spaceRequired) throws IOException, InterruptedException;
    String stateInformation() throws IOException, InterruptedException;

}