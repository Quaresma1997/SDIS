package subprotocols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import communication.Peer;
import files_data.Chunk;
import files_data.FileData;
import message.*;
import subprotocolsInitiators.BackupInitiator;
import subprotocolsInitiators.SubprotocolInitiator;
import utils.Utils;

public class SpaceReclaim implements Runnable {
    private MessageHeader msgHeader;
    

    public SpaceReclaim(Message message) {
        this.msgHeader = message.getHeader();

        System.out.println("Space Reclaim started");
    }

    @Override
    public void run() {
        int peerID = msgHeader.getPeerID();
        String fileID = msgHeader.getFileID();
        int chunkNum = msgHeader.getChunkNum();

        Chunk chunk = Peer.getFileHandler().getStoredChunk(fileID, chunkNum);

        // If the chunk is not in this peer then return
        if (chunk == null)
            return;

        chunk.removePeerID(peerID);
        // If the chunk is not backed up in this peer return
        if (!chunk.checkChunkFromPeer(Peer.getServerID()))
            return;


        // System.out.println("BACKUP Chunk " + chunkNum + "REP " + chunk.getRepDeg() + " FINAL " + chunk.getFinalRepDeg());
        // If the rep deg drops bellow the final rep reg, then this peer initiates the backup protocol for that chunk
        if (chunk.getRepDeg() < chunk.getFinalRepDeg()) {
            
            try {
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(401));

                // In the case that other peer initiates the backup protocol first, the this does nothing
                if (Peer.getFileHandler().getBackingUpChunks().contains(chunk))
                    return;

                FileData file = Peer.getFileHandler().getFileFromFileID(fileID);
                String chunkPath = Utils.TMP_CHUNKS + Peer.getServerID() + '/' + file.getFileID() + chunk.getChunkNum();
                Path path = Paths.get(chunkPath);
                byte[] fileData = Files.readAllBytes(path);
                chunk.setChunkData(fileData);
                Peer.getSubprotocolInitManager().getBackupInitiator().spaceReclaimStart(chunk, chunk.getFinalRepDeg(), fileData);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}