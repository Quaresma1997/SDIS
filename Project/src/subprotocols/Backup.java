package subprotocols;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import files_data.*;
import utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import message.*;

import communication.Peer;

public class Backup implements Runnable {

    private MessageHeader msgHeader;
    private MessageBody msgBody;

    public Backup(Message message) throws IOException {

        this.msgHeader = message.getHeader();
        this.msgBody = message.getBody();
        System.out.println("Backup started");
    }

    @Override
    public void run() {
        String protocol_version = msgHeader.getProtocolVersion();
        int repDeg = msgHeader.getRepDeg();
        String fileID = msgHeader.getFileID();
        int chunkNum = msgHeader.getChunkNum();

        byte[] chunkData = msgBody.getData();

        Chunk chunk = new Chunk(fileID, chunkNum, chunkData, repDeg);

        boolean enoughSpace = false;
        try {
            enoughSpace = checkEnoughSpace(chunk);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!enoughSpace)
            return;

        Peer.getFileHandler().addBackingUpChunk(chunk);
        FileData file = Peer.getFileFromHandlerStoredFileID(fileID);

        //Checks if this peer has called the backup for this chunk
        if(file != null){
            if(file.getFilePath() != ""){
                return;
            }
        }
       
        //Checks if this peer has already backed up this chunk
        if (Peer.getFileHandler().checkChunkStoredFromPeer(chunk, fileID, Peer.getServerID())) {
            Peer.getFileHandler().removeBackingUpChunk(chunk);
            System.out.println("Chunk " + chunkNum + " already stored. Aborting.");
            return;
        }

        FileOutputStream out = null;

        try {
            TimeUnit.MILLISECONDS.sleep(new Random().nextInt(401));

            if (protocol_version.equals("1.1")) {
                if (checkChunkRepDegGreaterThanFinal(fileID, chunkNum, repDeg)) {
                    Peer.getFileHandler().removeBackingUpChunk(chunk);
                    System.out.println("Chunk " + chunkNum + " not stored beacuse it's rep deg is greater than the final rep deg.");
                    return;
                }
            }
            Peer.getFileHandler().addFileChunkPutchunk(chunk, fileID);

            out = new FileOutputStream(Utils.TMP_CHUNKS + Peer.getServerID() + '/' + fileID + chunkNum);
            out.write(chunkData);
            out.close();

            MessageHeader newMsgHeader = new MessageHeader(Message.MessageType.STORED, protocol_version,
                    Peer.getServerID(), fileID, chunkNum);

            Peer.getMcChannel().sendMessage(newMsgHeader.getMessageHeaderStr().getBytes(Charset.forName("ISO_8859_1")));

            Peer.getFileHandler().removeBackingUpChunk(chunk);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean checkChunkRepDegGreaterThanFinal(String fileID, int chunkNum, int repDeg) {
        FileData file = Peer.getFileFromHandlerStoredFileID(fileID);

        if (file != null) {
            Chunk chunk = file.getChunk(chunkNum);

            if (chunk != null) {
                if (chunk.getRepDeg() >= repDeg){
                    // System.out.println("Chunk " + chunkNum + " Rep deg " + chunk.getRepDeg() + " " + repDeg);
                    return true;
                }

            }
        }
        return false;
    }

    private boolean checkEnoughSpace(Chunk chunk) throws IOException {

        if ((chunk.getChunkData().length + Peer.getOcupiedSpace()) > Peer.getSpaceAvailable() * 1000) {
            if (!Peer.getFileHandler().removeChunksGreaterRepDeg(
                    (chunk.getChunkData().length + Peer.getOcupiedSpace()) - Peer.getSpaceAvailable() * 1000)) {
                System.out.println("Chunk not stored due to lack of space in this peer!");
                return false;
            }
        }
        return true;
    }
}
