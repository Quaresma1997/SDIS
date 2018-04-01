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

        System.out.println("Start SpaceReclaim");
    }

    @Override
    public void run() {
        String protocol_version = msgHeader.getProtocolVersion();
        int peerID = msgHeader.getPeerID();
        String fileID = msgHeader.getFileID();
        int chunkNum = msgHeader.getChunkNum();

        Chunk chunk = Peer.getFileHandler().getStoredChunk(fileID, chunkNum);

        // If this peer doesn't have the chunk, it simply returns.
        if (chunk == null)
            return;

        chunk.removePeerID(peerID);

        if (!chunk.checkChunkFromPeer(Peer.getServerID())) {
            System.out.println("Curr: " + chunk.getRepDeg());
            System.out.println("Final: " + chunk.getFinalRepDeg());
            return;
        }

        System.out.println("FFA: " + chunk.getRepDeg());
        System.out.println("FIII: " + chunk.getFinalRepDeg());

        // If the new replication degree is less than the desired, we need to get it back to that number.
        if (chunk.getRepDeg() < chunk.getFinalRepDeg()) {
            try {
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(401));

                System.out.println("A");

                // If this peer has already received the message to backup this exact chunk,
                // we don't send the message.
                if (Peer.getFileHandler().getBackingUpChunks().contains(chunk))
                    return;

                System.out.println("B");

                FileData file = Peer.getFileHandler().getFileFromFileID(fileID);
                System.out.println(file);
                System.out.println(Peer.getFileHandler().getStored());
                String chunkPath = Utils.TMP_CHUNKS + Peer.getServerID() + '/' + file.getFileID() + chunk.getChunkNum();
                Path path = Paths.get(chunkPath);
                byte[] fileData = Files.readAllBytes(path);
                chunk.setChunkData(fileData);
                System.out.println(fileData);
                Peer.getSubprotocolInitManager().getBackupInitiator().spaceReclaimStart(chunk, chunk.getFinalRepDeg(), fileData);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}