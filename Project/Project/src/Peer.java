import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Peer{
	public static void main(String[] args) throws IOException, UnknownHostException, InterruptedException{


		int id, fileId;
		InetAddress MDB_ip, MC_ip;
		int MDB_port, MC_port;
		String filename;
		int rep_degree;

		int sizeOfFiles = 64000;// 64KB
		byte[] buffer = new byte[sizeOfFiles];

		MC_ip = InetAddress.getByName(args[0].trim());
		MDB_ip = InetAddress.getByName(args[1].trim());

		System.out.println(MC_ip);
		System.out.println(args[0]);

		MC_port = MDB_port = 8080;

		MulticastSocket mcastSocket_MC = new MulticastSocket(MC_port);
		mcastSocket_MC.joinGroup(MC_ip);

		// MulticastSocket mcastSocket_MDB = new MulticastSocket(MDB_port);
		// mcastSocket_MDB.joinGroup(MDB_ip);
		filename="./test.txt";
		File f = new File(filename);
		
		try (FileInputStream fis = new FileInputStream(f);
				BufferedInputStream bis = new BufferedInputStream(fis)) {

			int bytesAmount = 0;
			bytesAmount = bis.read(buffer);

		}

		byte[] sbuf = new byte[256];
		byte[] resbuf = new byte[256];
		String message = "PUTCHUNK";
		String initiator = args[2].trim();
		System.out.println(initiator);

		if(initiator.compareTo("NICE") == 0){

			System.out.println("AAAAA");

			
			sbuf = message.getBytes();
			DatagramPacket spacket = new DatagramPacket(sbuf, sbuf.length, MC_ip, MC_port);
			mcastSocket_MC.send(spacket);
			DatagramPacket respacket = new DatagramPacket(resbuf, resbuf.length);
			mcastSocket_MC.receive(respacket);
			
		}else{

			System.out.println("BBBB");

			DatagramPacket respacket = new DatagramPacket(resbuf, resbuf.length);
			mcastSocket_MC.receive(respacket);
			sbuf = message.getBytes();
			DatagramPacket spacket = new DatagramPacket(sbuf, sbuf.length, MC_ip, MC_port);
			mcastSocket_MC.send(spacket);
	
		}
		mcastSocket_MC.leaveGroup(MC_ip);
		mcastSocket_MC.close();

		//Message putChunkMessage = new Message("PUTCHUNK", "1.0", id, fileId, 0, 1, bytesAmount.getBytes());


	}
}