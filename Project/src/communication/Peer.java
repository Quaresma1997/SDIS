package communication;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import communication.RMI;

// import channels.*;

public class Peer implements RMI{


	// private MC_channel mc_channel;
	// private MDB_channel mdb_channel;
	// private MDR_channel mdr_channel;
	
	private static int serverID;

	// InetAddress MC_ip, MDB_ip, MDR_ip;
	// int MC_port, MDB_port, MDR_port;

		private static InetAddress mcAddress;
	private static int mcPort;

	private static InetAddress mdbAddress;
	private static int mdbPort;

	private static InetAddress mdrAddress;
	private static int mdrPort;

	private static String remoteObjName = "TestRMI";

	public static void main(String[] args) throws IOException{

		 try {
            Peer obj = new Peer();
            RMI stub = (RMI) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind(remoteObjName, stub);

            System.err.println("Peer ready");
        } catch (Exception e) {
            System.err.println("Peer exception: " + e.toString());
            e.printStackTrace();
        }

		// if(validateArgs(args) == -1)
		// 	return;

		
		// // multicast control channel
		// MulticastSocket mcSocket = new MulticastSocket();
		// mcSocket.setTimeToLive(1);

		// // multicast data backup channel
		// MulticastSocket mdbSocket = new MulticastSocket();
		// mdbSocket.setTimeToLive(1);

		// // multicast data restore channel
		// MulticastSocket mdrSocket = new MulticastSocket();
		// mdrSocket.setTimeToLive(1);

		// String test;
		// DatagramPacket packet;

		// test = "mc test";
		// packet = new DatagramPacket(test.getBytes(), test.getBytes().length,
		// 		mcAddress, mcPort);
		// mcSocket.send(packet);

		// test = "mdb test";
		// packet = new DatagramPacket(test.getBytes(), test.getBytes().length,
		// 		mdbAddress, mdbPort);
		// mdbSocket.send(packet);

		// test = "mdr test";
		// packet = new DatagramPacket(test.getBytes(), test.getBytes().length,
		// 		mdrAddress, mdrPort);
		// mdrSocket.send(packet);

		// mcSocket.close();
		// mdbSocket.close();
		// mdrSocket.close();

		// System.out.println("- done -");

		
		// new Thread(mc_channel).start();
		// new Thread(mdb_channel).start();
		// new Thread(mdr_channel).start();

	}

	public String test() throws RemoteException{
		return "TEST";
	}

	private static int validateArgs(String[] args) throws UnknownHostException{
		

		if(args.length != 7){
			System.out.println("Usage: java peer.Peer <Server ID> <MC_addr> <MC_port> <MDB_addr> <MDB_port> <MDR_addr> <MDR_port>");
			return -1;
		}else{
			mcAddress = InetAddress.getByName(args[1]);
			mcPort = Integer.parseInt(args[2]);

			mdbAddress = InetAddress.getByName(args[3]);
			mdbPort = Integer.parseInt(args[4]);

			mdrAddress = InetAddress.getByName(args[5]);
			mdrPort = Integer.parseInt(args[6]);


			serverID = Integer.parseInt(args[0].trim());

			// MC_ip = InetAddress.getByName(args[1].trim());
			// MC_port = Integer.parseInt(args[2].trim());

			// MDB_ip = InetAddress.getByName(args[3].trim());
			// MDB_port = Integer.parseInt(args[4].trim());

			// MDR_ip = InetAddress.getByName(args[5].trim());
			// MDR_port = Integer.parseInt(args[6].trim());

			// mc_channel = new MC_channel(MC_port, MC_ip);
			// mdb_channel = new MDB_channel(MDB_port, MDB_ip);
			// mdr_channel = new MDR_channel(MDR_port, MDR_ip);
		}

		return 1;
	}

	// public MC_channel getMcChannel(){
	// 	return mc_channel;
	// }

	// public MDB_channel getMdbChannel(){
	// 	return mdb_channel;
	// }

	// public MDR_channel getMdrChannel(){
	// 	return mdr_channel;
	// }

	public int getServerID(){
		return serverID;
	}
}