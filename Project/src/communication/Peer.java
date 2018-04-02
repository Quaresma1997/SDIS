package communication;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import communication.RMI;
import files_data.*;
import message.MessageBody;
import message.MessageHeader;
import message.Message;
import subprotocols.*;
import subprotocolsInitiators.*;
import utils.*;

import channels.*;

public class Peer implements RMI {

	private static MC_channel mc_channel;
	private static MDB_channel mdb_channel;
	private static MDR_channel mdr_channel;

	private static String protocol_version;
	private static int server_id;
	private static String service_access_point;

	private static FileHandler fileHandler;
	private static SubprotocolManager subprotocolManager;
	private static SubprotocolInitManager subprotocolInitManager;

	private static long spaceAvailable;

	public static void main(String[] args) throws IOException, InterruptedException {

		if (!validateArgs(args))
			return;

		createPeerDirectories();

		initiateRMI();

		spaceAvailable = Utils.MAX_DISK_REQUIRED_SPACE;

		subprotocolManager = new SubprotocolManager();

		subprotocolInitManager = new SubprotocolInitManager(protocol_version);

		fileHandler = new FileHandler();

		new Thread(mc_channel).start();
		new Thread(mdb_channel).start();
		new Thread(mdr_channel).start();

		new Thread(subprotocolManager).start();

	}

	private static void createPeerDirectories() {
		File chunkDir = new File(Utils.TMP_CHUNKS + server_id);
		if (chunkDir.exists()) {
			try {
				Utils.deleteDirectory(chunkDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		chunkDir.mkdir();

		File restoreDir = new File(Utils.TMP_FILES_RESTORED + server_id);
		if (restoreDir.exists()) {
			try {
				Utils.deleteDirectory(restoreDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		restoreDir.mkdir();
	}

	private static void initiateRMI() {
		Peer peer = new Peer();
		try {
			RMI stub = (RMI) UnicastRemoteObject.exportObject(peer, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind(service_access_point, stub);
			System.out.println("Peer ready");
		} catch (Exception e) {
			System.err.println("Peer exception: " + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void backup(String filePath, int repDeg, byte[] fileData) throws IOException {
		subprotocolInitManager.getBackupInitiator().setFilePath(filePath);
		subprotocolInitManager.getBackupInitiator().setRepDeg(repDeg);
		subprotocolInitManager.getBackupInitiator().setFileData(fileData);

		subprotocolInitManager.getBackupInitiator().initiate();
	}

	@Override
	public void restore(String filePath) throws IOException {
		subprotocolInitManager.getRestoreInitiator().setFilePath(filePath);
		subprotocolInitManager.getRestoreInitiator().initiate();
	}

	@Override
	public void delete(String filePath) throws IOException {
		subprotocolInitManager.getDeleteInitiator().setFilePath(filePath);
		subprotocolInitManager.getDeleteInitiator().initiate();

	}

	@Override
	public void reclaimDiskSpace(long spaceRequired) throws IOException {
		subprotocolInitManager.getSpaceReclaimInitiator().setSpaceRequired(spaceRequired);
		subprotocolInitManager.getSpaceReclaimInitiator().initiate();

	}

	@Override
	public String stateInformation() throws IOException {
		subprotocolInitManager.getStateInformationInitiator().initiate();
		String str = subprotocolInitManager.getStateInformationInitiator().getMessage();

		return str;
	}

	private static boolean validateArgs(String[] args) throws UnknownHostException, IOException {
		InetAddress MC_ip, MDB_ip, MDR_ip;
		int MC_port, MDB_port, MDR_port;

		if (args.length != 6) {
			System.out.println(
					"Usage: java communication/Peer <protocol_version> <server_id> <service_access_point> <mc:mc_port> <mdb:mdb_port> <mdl:mdl_port>");
			return false;
		} else {
			protocol_version = args[0].trim();

			server_id = Integer.parseInt(args[1].trim());

			service_access_point = args[2].trim();

			String[] mc_address = args[3].trim().split(":");
			MC_ip = InetAddress.getByName(mc_address[0]);
			MC_port = Integer.parseInt(mc_address[1]);

			String[] mdb_address = args[4].trim().split(":");
			MDB_ip = InetAddress.getByName(mdb_address[0]);
			MDB_port = Integer.parseInt(mdb_address[1]);

			String[] mdr_address = args[5].trim().split(":");
			MDR_ip = InetAddress.getByName(mdr_address[0]);
			MDR_port = Integer.parseInt(mdr_address[1]);

			mc_channel = new MC_channel(MC_ip, MC_port);
			mdb_channel = new MDB_channel(MDB_ip, MDB_port);
			mdr_channel = new MDR_channel(MDR_ip, MDR_port);
		}

		return true;
	}

	public static void updateStored(Message message) {
		fileHandler.updateStoredChunks(message);
		subprotocolInitManager.getBackupInitiator().updateUploadingChunks(message);

	}

	public static void deleteFileStored(String fileID) throws IOException {
		fileHandler.deleteStoredFileChunks(fileID);
	}

	public static void deleteStoredChunk(String fileID, int chunkNum) throws IOException {
		fileHandler.deleteStoredChunk(fileID, chunkNum, server_id);
	}

	public static void restoreGetChunk(Message message) {
		if (!subprotocolInitManager.getRestoreInitiator().getIsRestoring())
			fileHandler.checkRestoringChunk(message);
		subprotocolInitManager.getRestoreInitiator().addChunkRestore(message);
	}

	public static int getServerID() {
		return server_id;
	}

	public static void setServerID(int serverID) {
		server_id = serverID;
	}

	public static String getProtocolVersion() {
		return protocol_version;
	}

	public static void setProtocolVersion(String protocolVersion) {
		protocol_version = protocolVersion;
	}

	public static String getServiceAccessPoint() {
		return service_access_point;
	}

	public static void setServiceAccessPoint(String serviceAcessPoint) {
		service_access_point = serviceAcessPoint;
	}

	public static long getSpaceAvailable() {
		return spaceAvailable;
	}

	public static void setSpaceAvailable(long space) {
		spaceAvailable = space;
	}

	public static long getOcupiedSpace() throws IOException {
		return fileHandler.getStoredUsedSpace();
	}

	public static ArrayList<Chunk> getOrderedStoredChunks() {
		return fileHandler.getOrderedByRepDegChunks();
	}

	public static FileHandler getFileHandler() {
		return fileHandler;
	}

	public static void setFileHandler(FileHandler handler) {
		fileHandler = handler;
	}

	public static void addFileToHandlerStored(FileData file) {
		fileHandler.addFileStored(file);
	}

	public static FileData getFileFromHandlerStored(String filePath) {
		return fileHandler.getFileFromFilePath(filePath);
	}

	public static FileData getFileFromHandlerStoredFileID(String fileID) {
		return fileHandler.getFileFromFileID(fileID);
	}

	public static SubprotocolManager getSubprotocolManager() {
		return subprotocolManager;
	}

	public static void setSubprotocolManager(SubprotocolManager manager) {
		subprotocolManager = manager;
	}

	public static SubprotocolInitManager getSubprotocolInitManager() {
		return subprotocolInitManager;
	}

	public static void setSubprotocolInitManager(SubprotocolInitManager manager) {
		subprotocolInitManager = manager;
	}

	public static MC_channel getMcChannel() {
		return mc_channel;
	}

	public static MDB_channel getMdbChannel() {
		return mdb_channel;
	}

	public static MDR_channel getMdrChannel() {
		return mdr_channel;
	}
}