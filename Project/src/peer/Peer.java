package peer;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import sockets.*;

public class Peer{


	static MC_socket mc_socket;
	static MDB_socket mdb_socket;

	public static void main(String[] args) throws IOException, UnknownHostException, InterruptedException{

		InetAddress MC_ip, MDB_ip;
		int MC_port, MDB_port;

		if(args.length != 4){
			System.out.println("\tjava peer.Peer <MC_addr> <MC_port> <MDB_addr> <MDB_port>");
			return;
		}else{
			MC_ip = InetAddress.getByName(args[0].trim());
			MC_port = Integer.parseInt(args[1].trim());

			MDB_ip = InetAddress.getByName(args[2].trim());
			MDB_port = Integer.parseInt(args[3].trim());

			mc_socket = new MC_socket(MC_port, MC_ip);
			mdb_socket = new MDB_socket(MDB_port, MDB_ip);

		}
		
		new Thread(mc_socket).start();
		new Thread(mdb_socket).start();




	}
}