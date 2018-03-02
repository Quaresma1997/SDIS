import package message;

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
		String MDB_ip, MC_ip;
		int MDB_port, MC_port;
		String filename;
		int rep_degree;

		int sizeOfFiles = 64000;// 64KB
		byte[] buffer = new byte[sizeOfFiles];

		MulticastSocket mcastSocket_MC = new MulticastSocket(MC_port);
		mcastSocket_MC.joinGroup(MC_ip);

		MulticastSocket mcastSocket_MDB = new MulticastSocket(MDB_port);
		mcastSocket_MDB.joinGroup(MDB_ip);

		File f = new File(filename);

		try (FileInputStream fis = new FileInputStream(f);
				BufferedInputStream bis = new BufferedInputStream(fis)) {

			int bytesAmount = 0;
			bytesAmount = bis.read(buffer));

		}
		


		Message putChunkMessage = new Message("PUTCHUNK", "1.0", id, fileId, 0, 1, bytesAmount.getBytes());


	}
}