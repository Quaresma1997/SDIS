package communication;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;

public interface RMI extends Remote {

    void backup(String filepath, int repDeg, byte[] fileData) throws IOException;

    void restore(String filepath) throws IOException;

    void delete(String filepath) throws IOException;

    void reclaimDiskSpace(long spaceRequired) throws IOException;
    
    String stateInformation() throws IOException;

}