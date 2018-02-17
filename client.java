import java.io.IOException;
import java.net.*;

public class client {

	public static void main(String args[]) throws IOException{
		
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
		System.out.println(message);
		InetAddress address = InetAddress.getByName(hostname);
		DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, port);
		socket.send(packet);
		
		byte[] rbuf = new byte[sbuf.length];
		packet = new DatagramPacket(rbuf, rbuf.length);
		socket.receive(packet);
		
		String received = new String(packet.getData());
		System.out.println("Message: " + received);
		
		socket.close();
	}
}
