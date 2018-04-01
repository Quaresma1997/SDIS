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
        long freeSpace = 0;
        String os_name = System.getProperty("os.name");
        if(os_name.startsWith("Windows"))
            freeSpace = new File("C:").getFreeSpace();
        else if(os_name.startsWith("Linux"))
            freeSpace = new File("/").getFreeSpace();
        
        System.out.println(Peer.getOcupiedSpace());

        if (spaceRequired * 1000 > freeSpace) {
            System.out.println("Space required is more than the available space in the machine!");
            return;
        }

        if (spaceRequired * 1000 < Peer.getOcupiedSpace()) {
            removeChunksWithMoreRepDeg();
        }

        Peer.setSpaceReclaimed(spaceRequired);

        Peer.getSubprotocolInitManager().resetSpaceReclaimInitiator();
    }

    private void removeChunksWithMoreRepDeg() throws IOException{ 
        ArrayList<Chunk> chunkList = Peer.getOrderedStoredChunks();
        int numChunksRemoved = 0;
        Chunk chunkRemove = chunkList.get(numChunksRemoved);
        System.out.println(chunkList.size());
        System.out.println(chunkRemove.getFileID());
        do {
            FileData file = Peer.getFileFromHandlerStoredFileID(chunkRemove.getFileID());
            String fileID = file.getFileID();

            MessageHeader msgHeader = new MessageHeader(Message.MessageType.REMOVED, Peer.getProtocolVersion(),
                    Peer.getServerID(), fileID, chunkRemove.getChunkNum());
            Message message = new Message(msgHeader);
            byte[] buffer = message.getMessageBytes();
            Peer.sendMCMessage(buffer);
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