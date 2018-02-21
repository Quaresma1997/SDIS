package cars;
import java.io.IOException;
import java.net.*;



public class Server {
		
	public static void main(String args[]) throws IOException{
		
		System.out.println("\nSERVER\n");
		
		int port = 4445;
		DatagramSocket socket = new DatagramSocket(port);
		
		boolean done = false;
		
		Operation op = new Operation();
				
		while(!done) {
			
			byte[] rbuf = new byte[256];
			int clientPort;
			byte[] clientData;
			InetAddress clientAddress;
			String received;
			String[] message;
			String oper;
			byte[] sbuf = new byte[256];
			
			DatagramPacket rpacket = new DatagramPacket(rbuf, rbuf.length);
			
			try {
				socket.setSoTimeout(10*1000);		
				socket.receive(rpacket);
			}catch(Exception SocketTimeoutException) {
				System.out.println("SERVER TIME OUT!\n");
				break;
			}
									
			clientPort = rpacket.getPort();
			clientData = rpacket.getData();
			clientAddress = rpacket.getAddress();
			received = new String(clientData).trim();
			message = received.split(":");
			
			oper = message[0];
			
			System.out.println("Server received: " + received + "\n");
			
			switch(oper) {
			case "REGISTER":
				sbuf = op.regist(message[1], message[2]);
				break;
			case "LOOKUP":
				sbuf = op.lookup(message[1]);
				break;
			default:
				sbuf = "ERROR".getBytes();
				System.out.println("The requested operation is not valid!\n\n");
				break;
			}
			
			DatagramPacket spacket = new DatagramPacket(sbuf, sbuf.length, clientAddress, clientPort);
			socket.send(spacket);	
		}
		
		socket.close();
		
	}
	
	

}
