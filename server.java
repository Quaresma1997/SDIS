import java.io.IOException;
import java.net.*;

public class server {
	
	public static void main(String args[]) throws IOException{
		
		int port = Integer.parseInt(args[0]);
		DatagramSocket socket = new DatagramSocket(port);
		
		boolean done = false;
		
		while(!done) {
			System.out.println("server");
			
			byte[] rbuf = new byte[256];
			DatagramPacket rpacket = new DatagramPacket(rbuf, rbuf.length);
			
			
			socket.receive(rpacket);
						
			int clientPort = rpacket.getPort();
			byte[] clientData = rpacket.getData();
			InetAddress clientAddress = rpacket.getAddress();
			String received = new String(rpacket.getData()).trim();
			String[] message = received.split(":");
			
			System.out.println("RECEIVED: " + received);
			
			
			
			byte[] sbuf = "OK".getBytes();
			DatagramPacket spacket = new DatagramPacket(sbuf, sbuf.length, clientAddress, clientPort);
			socket.send(spacket);
			done = true;
		}
		
		socket.close();
		
	}

}
