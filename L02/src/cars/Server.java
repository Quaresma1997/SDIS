package cars;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Server {

	public static void main(String[] args) throws IOException, UnknownHostException, InterruptedException {

		int srvc_port = Integer.parseInt(args[0]);
		InetAddress mcast_addr = InetAddress.getByName(args[1]);
		int mcast_port = Integer.parseInt(args[2]);

		DatagramSocket serverSocket = new DatagramSocket(srvc_port);
		serverSocket.setSoTimeout(1000);
		
		SocketAddress srvc_addr = serverSocket.getLocalSocketAddress();

		MulticastSocket mcastSocket = new MulticastSocket();
		mcastSocket.setTimeToLive(1);

		byte[] clientData = new byte[256];
		String received = null;
		String[] message = new String[3];
		String oper = null;
		String[] opnd = new String[2];
		String out = null;
		byte[] sbuf = new byte[256];

		boolean done = false;

		Operation op = new Operation();

		while(!done) {

			byte[] rbuf = new byte[256];
			DatagramPacket rpacket = new DatagramPacket(rbuf, rbuf.length);

			try {		
				serverSocket.receive(rpacket);

				clientData = rpacket.getData();
				received = new String(clientData).trim();
				message = received.split(":");

				oper = message[0];

				switch(oper) {
				case "REGISTER":
					opnd[0] = message[1];
					opnd[1] = message[2];
					sbuf = op.regist(opnd[0], opnd[1]);
					System.out.println("Server received: " + oper + " " + opnd + " :: " + sbuf + "\n");
					break;
				case "LOOKUP":
					opnd[0] = message[1];
					sbuf = op.lookup(message[1]);
					System.out.println("Server received: " + oper + " " + opnd + " :: " + sbuf + "\n");
					break;
				default:
					sbuf = "ERROR".getBytes();
					System.out.println("The requested operation is not valid!\n\n");
					break;
				}
				
				DatagramPacket spacket = new DatagramPacket(sbuf, sbuf.length, mcast_addr, mcast_port);
				serverSocket.send(spacket);	

			}catch(SocketTimeoutException ex) {
				
				String ad = srvc_addr + ":" + srvc_port;
				DatagramPacket adPacket = new DatagramPacket(ad.getBytes(), ad.getBytes().length, mcast_addr, mcast_port);
				
				mcastSocket.send(adPacket);
				
				System.out.println("multicast: " + mcast_addr + " " + mcast_port + " : " + srvc_addr + " " + srvc_port);
			}

		}
		
		serverSocket.close();
		
		mcastSocket.close();
	}
}


