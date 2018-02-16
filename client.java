import java.io.IOException;
import java.net.*;

public class client {

	public static void main(String args[]) throws IOException{

		DatagramSocket socket = new DatagramSocket();
		byte[] sbuf = "zzz".getBytes();
		InetAddress address = InetAddress.getByName("hostname");
		DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, 4445);
		socket.send(packet);
		
		byte[] rbuf = new byte[sbuf.length];
		packet = new DatagramPacket(rbuf, rbuf.length);
		socket.receive(packet);
		
		String received = new String(packet.getData());
		System.out.println("Message: " + received);
		
		socket.close();
	}
}
