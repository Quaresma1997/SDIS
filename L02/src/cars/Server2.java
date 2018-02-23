package cars;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class Server2 {

	
	static class Task extends TimerTask {
		
		InetAddress srvc_addr;
		int srvc_port;
		MulticastSocket mcastSocket;
		InetAddress mcast_addr;
		int mcast_port;

		public Task(InetAddress srvc_addr, int srvc_port, MulticastSocket mcastSocket, InetAddress mcast_addr,
				int mcast_port) {
			this.srvc_addr = srvc_addr;
			this.srvc_port = srvc_port;
			this.mcastSocket = mcastSocket;
			this.mcast_addr = mcast_addr;
			this.mcast_port = mcast_port;
		}

		@Override
		public void run() {
			String ad = srvc_addr + ":" + srvc_port;
			DatagramPacket adPacket = new DatagramPacket(ad.getBytes(), ad.getBytes().length, mcast_addr, mcast_port);
			
			try {
				mcastSocket.send(adPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("multicast: " + mcast_addr + " " + mcast_port + " : " + srvc_addr + " " + srvc_port);
			
		}
	}
	
	public static void main(String[] args) throws IOException, UnknownHostException, InterruptedException{
		
		System.out.println("\nSERVER\n");

		int srvc_port = Integer.parseInt(args[0].trim());
		InetAddress mcast_addr = InetAddress.getByName(args[1].trim());
		int mcast_port = Integer.parseInt(args[2].trim());

		DatagramSocket serverSocket = new DatagramSocket(srvc_port);
		
		InetAddress srvc_addr = InetAddress.getLocalHost();

		MulticastSocket mcastSocket = new MulticastSocket();
		mcastSocket.setTimeToLive(1);

		byte[] clientData = new byte[256];
		String received = null;
		String[] message = new String[256];
		String oper = null;
		String[] opnd = new String[256];
		byte[] sbuf = new byte[256];

		boolean done = false;

		Operation op = new Operation();		
		
		Task task = new Task(srvc_addr, srvc_port, mcastSocket, mcast_addr, mcast_port);
		
		Timer timer = new Timer();
		timer.schedule(task, 0, 1000);

		while(!done) {

			byte[] rbuf = new byte[256];
			DatagramPacket rpacket = new DatagramPacket(rbuf, rbuf.length);

				serverSocket.receive(rpacket);

				clientData = rpacket.getData();
				received = new String(clientData).trim();
				message = received.split(":");
				
				oper = message[0];
				
				String result;

				switch(oper) {
				case "REGISTER":
					opnd[0] = message[1];
					opnd[1] = message[2];
					sbuf = op.regist(opnd[0], opnd[1]);
					result = new String(sbuf);
					System.out.println("Server received: " + oper + " " + opnd[0] + " " + opnd[1] + " :: " + result + "\n");
					break;
				case "LOOKUP":
					opnd[0] = message[1];
					sbuf = op.lookup(message[1]);
					result = new String(sbuf);
					System.out.println("Server received: " + oper + " " + opnd[0] + " " + opnd[1] + " :: " + result + "\n");
					break;
				default:
					sbuf = "ERROR".getBytes();
					System.out.println("The requested operation is not valid!\n\n");
					break;
				}
				
				DatagramPacket spacket = new DatagramPacket(sbuf, sbuf.length, mcast_addr, mcast_port);
				serverSocket.send(spacket);	
				
			
		}
		
		serverSocket.close();
		
		mcastSocket.close();
	}
}

