import java.io.IOException;
import java.net.*;

public class server {
	
	public static void main(String args[]) throws IOException{
		
		int port = 4445;
		DatagramSocket socket = new DatagramSocket(port);
		byte[] rbuf = "zzz".getBytes();
		DatagramPacket rpacket = new DatagramPacket(rbuf, rbuf.length);

		boolean done = false;
		while(!done) {
			System.out.println("server");
			
			socket.receive(rpacket);
			
			System.out.println("received");
			
			int clientPort = rpacket.getPort();
			byte[] clientData = rpacket.getData();
			InetAddress clientAddress = rpacket.getAddress();
			
			
			
			byte[] sbuf = "OK".getBytes();
			DatagramPacket spacket = new DatagramPacket(sbuf, sbuf.length, clientAddress, clientPort);
			socket.send(spacket);
		}
		
	}

}
