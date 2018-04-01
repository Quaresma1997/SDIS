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

    public RestoreInitiator(String protocol_version) throws IOException, InterruptedException {
        super(protocol_version);
        
        //initiate();

    }

    @Override
    public void initiate() throws IOException, InterruptedException {
        isRestoring = true;
        FileData file = Peer.getFileFromHandlerStored(filePath);
        if (file == null) {
            System.out.println("File has not started backup in this peer.");
            return;
        }

        String fileID = file.getFileID();
        int numChunks = file.getChunkList().size();
        System.out.println("FILE ID: " + fileID);
        
        for (int i = 0; i < numChunks; i++) {
            MessageHeader msgHeader = new MessageHeader(Message.MessageType.GETCHUNK, protocol_version,
                    Peer.getServerID(), fileID, (i + 1));
            Message message = new Message(msgHeader);
            byte[] buffer = message.getMessageBytes();
            Peer.sendMCMessage(buffer);

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

        int numChunks = file.getChunkList().size();
        boolean foundAllChunks = false;
        long t = System.currentTimeMillis();
        long end = t + 2500;

        System.out.println("NUMC HUNKS: " + numChunks);

        

        while (System.currentTimeMillis() < end && !foundAllChunks) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (numChunks == restore.size())
                foundAllChunks = true;
        }

        if (!foundAllChunks) {
            System.out.println("Restore: Didn t find all chunks!");
            return;
        } else
            System.out.println("Restore: Found all chunks!");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        restore.sort(Comparator.comparingInt(Chunk::getChunkNum));
        
        for (int i = 0; i < restore.size() ; i++) {
            System.out.println(restore.get(i).getChunkNum());
            System.out.println(restore.get(i).getChunkData().length);
            try {
                outputStream.write(restore.get(i).getChunkData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] fileData = outputStream.toByteArray();
        FileOutputStream fos = new FileOutputStream(Utils.TMP_FILES_RESTORED + filePath);
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