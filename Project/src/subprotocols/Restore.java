package subprotocols;

import message.*;
import utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import communication.Peer;
import files_data.Chunk;
import files_data.FileData;

public class Restore implements Runnable {
    private MessageHeader msgHeader;

    public Restore(Message message) {
        this.msgHeader = message.getHeader();

        System.out.println("Start Restore");
    }

    @Override
    public void run() {
        String protocol_version = msgHeader.getProtocolVersion();
        String fileID = msgHeader.getFileID();
        int chunkNum = msgHeader.getChunkNum();


        FileData file = Peer.getFileHandler().getFileFromFileID(fileID);
        if (file == null)
            return;

        Chunk newChunk = new Chunk(fileID, chunkNum);
        for (Chunk chunk : file.getChunksFromPeer(Peer.getServerID())) {
            if (chunk.equals(newChunk)) {
                MessageHeader newMsgHeader = new MessageHeader(Message.MessageType.CHUNK, protocol_version,
                        Peer.getServerID(), fileID, chunkNum);

                Path path = Paths.get(Utils.TMP_CHUNKS + Peer.getServerID() + '/' + fileID + chunkNum);
                byte[] body = new byte[0];
                try {
                    body = Files.readAllBytes(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MessageBody msgBody = new MessageBody(body);

                Message message = new Message(newMsgHeader, msgBody);
                try {
                    byte[] buffer = message.getMessageBytes();

                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(401));

                    if (Peer.getFileHandler().getRestoringChunks().contains(chunk))
                        return;

                    Peer.sendMDRMessage(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}