package communication;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;

import communication.RMI;
import jdk.nashorn.internal.ir.CatchNode;

public class TestApp {

    private static final String LOCALHOST = "localhost";
    private static String hostname;
    private static String remoteObjName;
    private static String operation;
    private static int repDeg;
    private static long spaceRequired;
    private static String filePath;


    private static RMI stub;

    public static void main(String[] args) throws IOException {
        if (!validArgs(args))
            return;

        if (!initiateRMI())
            return;

        makeOperation();
    
        System.out.println("TestApp closed.");
    }

    private static void makeOperation() {
        switch (operation) {
        case "BACKUP":
            try {
                Path path = Paths.get(filePath);
                byte[] fileData = Files.readAllBytes(path);
                stub.backup(filePath, repDeg, fileData);
            } catch (InterruptedException | IOException | NoSuchAlgorithmException e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }

            break;
        case "RESTORE":
            try {
                System.out.println(filePath);
                stub.restore(filePath);
            } catch (IOException | InterruptedException e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
            break;
        case "DELETE":
            try {
                stub.delete(filePath);
            } catch (NoSuchAlgorithmException | InterruptedException | IOException e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
            break;
        case "RECLAIM":
            try {
                stub.reclaimDiskSpace(spaceRequired);
            } catch (IOException | InterruptedException e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
            break;
        case "STATE":
            try {
                System.out.println("Info: ");
                System.out.println(stub.stateInformation());
            } catch (IOException | InterruptedException e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
            break;
        default:
            break;
        }
    }

    private static boolean initiateRMI() {

        try {
            Registry registry = LocateRegistry.getRegistry(hostname);
            stub = (RMI) registry.lookup(remoteObjName);

        } catch (Exception e) {
            System.out.println("Given RMI object name is incorrect!");
            System.out.println(e.toString());

            return false;
        }

        return true;
    }

    private static boolean validArgs(String[] args) {

        if (args.length < 2 || args.length > 4) {
            System.out.println("Usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
            return false;
        }

        if (!getPeerAccessPoint(args[0]))
            return false;

        remoteObjName = args[0];
                

        return validOperation(args);
    }

    private static boolean getPeerAccessPoint(String peer_access_point) {
        String[] pap_split = peer_access_point.split(":");
        if (pap_split.length == 2) {
            hostname = pap_split[0];
            remoteObjName = pap_split[1];
        } else if (pap_split.length == 1) {
            hostname = LOCALHOST;
            remoteObjName = pap_split[0];
        } else {
            System.out.println("<peer_ap> must be either <Hostname>:<RemoteObjectName> or only <RemoteObjectName>!");
            return false;
        }

        return true;
    }

    private static boolean validOperation(String args[]) {
        operation = args[1];
        switch (operation) {
        case "BACKUP":

            if (args.length != 4) {
                System.out.println("Wrong num of args.");
                System.out.println("Usage: java TestApp <peer_ap> BACKUP <pathname> <replication_degree>");

                return false;
            }

            if (!checkFilePath(args[2]))
                return false;

            if (!checkRepDeg(args[3]))
                return false;
            break;
        case "RESTORE":
            if (args.length != 3) {
                System.out.println("Wrong num of args.");
                System.out.println("Usage: java TestApp <peer_ap> RESTORE <pathname>");

                return false;
            }

            filePath = args[2].trim();

            

            break;
        case "DELETE":
            if (args.length != 3) {
                System.out.println("Wrong num of args.");
                System.out.println("Usage: java TestApp <peer_ap> DELETE <pathname>");

                return false;
            }

            filePath = args[2].trim();

            break;
        case "RECLAIM":
            if (args.length != 3) {
                System.out.println("Wrong num of args.");
                System.out.println("Usage: java TestApp <peer_ap> RECLAIM <space_reclaim>");

                return false;
            }

            if (!checkSpaceInt(args[2]))
                return false;

            break;
        case "STATE":
            if (args.length != 2) {
                System.out.println("Wrong num of args.");
                System.out.println("Usage: java TestApp <peer_ap> STATE");

                return false;
            }

            break;
        default:
            System.out.println("Invalid operation. Choose between BACKUP, RESTORE, DELETE, RECLAIM and STATE");
            return false;
        }

        return true;
    }

    private static Boolean checkFilePath(String path) {

        File file = new File(path);

        filePath = path;

        if (!file.exists()) {
            System.out.println("File does not exist!");

            return false;
        } else if (!file.isFile()) {
            System.out.println("The given path is a directory, not a file!");

            return false;
        }
        return true;
    }

    private static Boolean checkRepDeg(String repDegString) {
        try {
            repDeg = Integer.parseInt(repDegString);

            // if(repDeg < 1)
            //     return false;
        } catch (NumberFormatException e) {
            System.out.println("The replication degree must be an integer greater than 0!");

            e.printStackTrace();

            return false;
        }

        return true;
    }

    private static Boolean checkSpaceInt(String space) {
        try {
            spaceRequired = Long.parseLong(space);

            // if(spaceRequired < 1)
            //     return false;
        } catch (NumberFormatException e) {
            System.out.println("The space given must be an integer greater than 0!");

            e.printStackTrace();

            return false;
        }

        return true;
    }

}