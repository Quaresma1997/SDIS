package subprotocolsInitiators;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import communication.Peer;
import files_data.Chunk;
import files_data.FileData;
import message.*;

public class SpaceReclaimInitiator extends SubprotocolInitiator{
    private long spaceRequired;

    
    public SpaceReclaimInitiator(String protocol_version) throws IOException{
        super(protocol_version);       
    }

    @Override
    public void initiate() throws IOException{
        if (spaceRequired * 1000 > Peer.getSpaceAvailable() * 1000) {
            System.out.println("Space required is more than the available space in the peer!");
            return;
        }

        if (spaceRequired * 1000 < Peer.getOcupiedSpace()) {
            removeChunksWithMoreRepDeg();
        }

        Peer.setSpaceAvailable(spaceRequired);

        Peer.getSubprotocolInitManager().resetSpaceReclaimInitiator();
    }

    private void removeChunksWithMoreRepDeg() throws IOException{ 
        ArrayList<Chunk> chunkList = Peer.getOrderedStoredChunks();
        int numChunksRemoved = 0;
        Chunk chunkRemove = chunkList.get(numChunksRemoved);
        do {
            FileData file = Peer.getFileFromHandlerStoredFileID(chunkRemove.getFileID());
            String fileID = file.getFileID();

            MessageHeader msgHeader = new MessageHeader(Message.MessageType.REMOVED, Peer.getProtocolVersion(),
                    Peer.getServerID(), fileID, chunkRemove.getChunkNum());
            Message message = new Message(msgHeader);
            byte[] buffer = message.getMsgBytes();
            Peer.getMcChannel().sendMessage(buffer);
            Peer.deleteStoredChunk(fileID, chunkRemove.getChunkNum());

            numChunksRemoved++;
            if (numChunksRemoved == chunkList.size()) {
                System.out.println("Removed all peer chunks.");
                return;
            }
            chunkRemove = chunkList.get(numChunksRemoved);
        } while (spaceRequired * 1000 < Peer.getOcupiedSpace());

        System.out.println("Space updated");

    }

    public void setSpaceRequired(long spaceRequired){
        this.spaceRequired = spaceRequired;
    }
}