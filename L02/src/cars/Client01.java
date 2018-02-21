package cars;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class Client01 {

	public static void main(String args[]) throws IOException{

		System.out.println("\nCLIENT\n");

		String hostname = args[0].trim();
		int port = Integer.parseInt(args[1].trim());
		String oper = args[2].trim();
		String plateNumber;
		String ownerName;
		String message = "";

		if(oper.compareTo("register") == 0) {
			plateNumber = args[3].trim();
			ownerName = args[4].trim();
			message = "REGISTER:" + plateNumber + ':' + ownerName; 
		}else if(oper.compareTo("lookup") == 0){
			plateNumber = args[3].trim();
			message = "LOOKUP:" + plateNumber;
		}


		DatagramSocket socket = new DatagramSocket();
		byte[] sbuf = message.getBytes();

		InetAddress address = InetAddress.getByName(hostname);
		DatagramPacket spacket = new DatagramPacket(sbuf, sbuf.length, address, port);
		socket.send(spacket);

		byte[] rbuf = new byte[256];
		DatagramPacket rpacket = new DatagramPacket(rbuf, rbuf.length);

		int tries = 3;

		while(tries > 0) {
			try {
				socket.setSoTimeout(5*1000);	
				socket.receive(rpacket);
				if(rpacket != null) {
					break;
				}
			}catch(Exception SocketTimeoutException) {
				socket.send(spacket);
				tries--;
				System.out.println("CLIENT TRY AGAIN! Remaining tries: " + tries + "\n");
			}
		}

		if(tries == 0) {
			System.out.println("CLIENT TIME OUT!\n");
		}


		String received = new String(rpacket.getData()).trim();

		if((received.compareTo("-1") == 0) || (received.compareTo("NOT_FOUND") == 0))
			received = "ERROR";

		String new_message = "REQUEST: " + message + "\n" + "RESULT: " + received + "\n";
		System.out.println(new_message);

		socket.close();
	}
}