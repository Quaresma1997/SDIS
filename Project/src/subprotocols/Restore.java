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

        System.out.println("Restore started");
    }

    @Override
    public void run() {
        String protocol_version = msgHeader.getProtocolVersion();
        String fileID = msgHeader.getFileID();
        int chunkNum = msgHeader.getChunkNum();

        FileData file = Peer.getFileHandler().getFileFromFileID(fileID);
        if (file == null){
            System.out.println("This peer does not have any chunks from the given fileID!");
            return;
        }
        Chunk chunk = new Chunk(chunkNum);
        sendChunks(file, chunk, protocol_version);       
    }

    private void sendChunks(FileData file, Chunk newChunk, String protocol_version){
        for (Chunk chunk : file.getChunksFromPeer(Peer.getServerID())) {
            if (chunk.equals(newChunk)) {
                MessageHeader newMsgHeader = new MessageHeader(Message.MessageType.CHUNK, protocol_version,
                        Peer.getServerID(), file.getFileID(), chunk.getChunkNum());

                Path path = Paths.get(Utils.TMP_CHUNKS + Peer.getServerID() + '/' + file.getFileID() + chunk.getChunkNum());
                byte[] chunkData = new byte[0];
                try {
                    chunkData = Files.readAllBytes(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MessageBody msgBody = new MessageBody(chunkData);
                Message message = new Message(newMsgHeader, msgBody);
                try {
                    byte[] buffer = message.getMsgBytes();

                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(401));

                    //If received CHUNK from another peer, than this chunk is already being restored
                    if (Peer.getFileHandler().getRestoringChunks().contains(chunk)){
                        Peer.getFileHandler().removeRestoringChunks(chunk);
                        return;
                    }

                    Peer.getMdrChannel().sendMessage(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }
}