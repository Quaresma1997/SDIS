package cars;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Client {

	public static void main(String args[]) throws IOException, UnknownHostException{

		System.out.println("\nCLIENT\n");

		InetAddress mcast_addr = InetAddress.getByName(args[0].trim());
		int mcast_port = Integer.parseInt(args[1].trim());
		String oper = args[2].trim();
		String plateNumber = null;
		String ownerName = null;
		String message = null;
		byte[] rbuf = new byte[256];
		byte[] sbuf = new byte[256];
		byte[] resbuf = new byte[256];
		byte[] serverData = new byte[256];
		String received = null;
		String[] ad = new String[2];
		int srvc_port;
		InetAddress srvc_addr = null;
		String result = null;
		
		if(oper.compareTo("register") == 0) {
			plateNumber = args[3].trim();
			ownerName = args[4].trim();
			message = "REGISTER:" + plateNumber + ':' + ownerName; 
		}else if(oper.compareTo("lookup") == 0){
			plateNumber = args[3].trim();
			message = "LOOKUP:" + plateNumber;
		}
		
		MulticastSocket mcastSocket = new MulticastSocket(mcast_port);
		mcastSocket.joinGroup(mcast_addr);

		DatagramPacket rpacket = new DatagramPacket(rbuf, rbuf.length);
		
		mcastSocket.receive(rpacket);
		
		InetSocketAddress srvc_socket = (InetSocketAddress) rpacket.getSocketAddress();
		srvc_addr = srvc_socket.getAddress();
		
		serverData = rpacket.getData();
		received = new String(serverData).trim();
		ad = received.split(":");
		srvc_port = Integer.parseInt(ad[1].trim());
		
		sbuf = message.getBytes();
		DatagramPacket spacket = new DatagramPacket(sbuf, sbuf.length, srvc_addr, srvc_port);
		mcastSocket.send(spacket);
		
		DatagramPacket respacket = new DatagramPacket(resbuf, resbuf.length);
		mcastSocket.receive(respacket);
		
		result = new String(respacket.getData()).trim();

		if((result.compareTo("-1") == 0) || (result.compareTo("NOT_FOUND") == 0))
			result = "ERROR";

		System.out.println("multicast: " + mcast_addr + " " + mcast_port + " : " + srvc_addr + " " + srvc_port);
		
		String new_message = "REQUEST: " + message + "\n" + "RESULT: " + result + "\n";
		System.out.println(new_message);
		
		mcastSocket.leaveGroup(mcast_addr);
		mcastSocket.close();
		
	}
}