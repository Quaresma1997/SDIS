package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import communication.RMI;

public class Client {

    private static InetAddress mcAddress;
    private static int mcPort;
    private static final String HOSTNAME = "localhost";
    private static String remoteObjName = "TestRMI";

    public static void main(String[] args) throws IOException {
        if (!validArgs(args))
            return;
            
        try {
            Registry registry = LocateRegistry.getRegistry(HOSTNAME);
            RMI stub = (RMI) registry.lookup(remoteObjName);
            String response = stub.test();
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        // MulticastSocket multicastSocket = new MulticastSocket(mcPort);
        // multicastSocket.joinGroup(mcAddress);

        // byte[] buf = new byte[256];
        // DatagramPacket multicastPacket = new DatagramPacket(buf, buf.length);
        // multicastSocket.receive(multicastPacket);

        // String msg = new String(multicastPacket.getData());

        // System.out.println("mgs: " + msg);

        // multicastSocket.leaveGroup(mcAddress);
        // multicastSocket.close();
    }

    private static boolean validArgs(String[] args) throws UnknownHostException {
        // mcAddress = InetAddress.getByName(args[0]);
        // mcPort = Integer.parseInt(args[1]);

        return true;
    }

}