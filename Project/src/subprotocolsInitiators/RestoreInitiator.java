package subprotocolsInitiators;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import communication.Peer;
import files_data.Chunk;
import files_data.FileData;
import message.*;
import utils.Utils;

public class RestoreInitiator extends SubprotocolInitiator {
    private ArrayList<Chunk> restore = new ArrayList<>();
    private String filePath;
    boolean isRestoring;

    public RestoreInitiator(String protocol_version) throws IOException {
        super(protocol_version);

    }

    @Override
    public void initiate() throws IOException {
        isRestoring = true;
        FileData file = Peer.getFileFromHandlerStored(filePath);
        if (file == null) {
            System.out.println("File has not started backup in this peer.");
            return;
        }

        String fileID = file.getFileID();
        int numChunks = file.getChunkList().size();
        
        for (int i = 0; i < numChunks; i++) {
            MessageHeader msgHeader = new MessageHeader(Message.MessageType.GETCHUNK, protocol_version,
                    Peer.getServerID(), fileID, (i + 1));
            Message message = new Message(msgHeader);
            byte[] buffer = message.getMsgBytes();
            Peer.getMcChannel().sendMessage(buffer);

        }

        restoreFile();

        isRestoring = false;

        Peer.getSubprotocolInitManager().resetRestoreInitiator();

    }

    public synchronized void addChunkRestore(Message message) {
        if (isRestoring) {
            MessageHeader msgHeader = message.getHeader();
            MessageBody msgBody = message.getBody();

            int chunkNum = msgHeader.getChunkNum();

            byte[] chunkData = msgBody.getData();

            Chunk chunk = new Chunk(chunkNum, chunkData);

            if (!restore.contains(chunk))
                restore.add(chunk);
        }

    }

    private void restoreFile() throws IOException {
        FileData file = Peer.getFileFromHandlerStored(filePath);

        
        boolean found = false;
        long t = System.currentTimeMillis();
        long timeLimit = t + 2500;      
        int numChunks = file.getChunkList().size();

        while (System.currentTimeMillis() < timeLimit && !found) {
            if (numChunks == restore.size())
                found = true;
        }

        if (!found) {
            System.out.println("Did not find all chunks to restore file!");
            return;
        } else
            System.out.println("Found all chunks to restore file!");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        restore.sort(Comparator.comparingInt(Chunk::getChunkNum));
        
        for (int i = 0; i < restore.size() ; i++) {
            try {
                outputStream.write(restore.get(i).getChunkData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] fileData = outputStream.toByteArray();
        FileOutputStream fos = new FileOutputStream(Utils.TMP_FILES_RESTORED + Peer.getServerID() + '/' + filePath);
        fos.write(fileData);
        fos.close();
        System.out.println("File successfully restored!");
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ArrayList<Chunk> getRestore() {
        return restore;
    }

    public void setRestore(ArrayList<Chunk> restore) {
        this.restore = restore;
    }
}