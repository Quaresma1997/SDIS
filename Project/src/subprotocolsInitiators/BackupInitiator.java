package subprotocolsInitiators;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import communication.Peer;
import files_data.Chunk;
import files_data.FileData;
import files_data.FileSplitter;
import message.*;
import utils.CryptoHash;
import utils.Utils;

public class BackupInitiator extends SubprotocolInitiator {
    private ArrayList<Chunk> upload = new ArrayList<>();
    private int numTransmissions;
    private byte[] fileData;
    private String filePath;
    private int repDeg;
    private boolean spaceReclaimCalled = false;
    private boolean isBacking = false;

    public BackupInitiator(String protocol_version) throws IOException {
        super(protocol_version);
        numTransmissions = 1;

    }

    @Override
    public void initiate() throws IOException {
        isBacking = true;

        String fileIDHashedStr = makeFileID();

        FileSplitter fileSplitter = new FileSplitter(fileData);
        try {
            fileSplitter.splitFileIntoChunks(fileIDHashedStr, repDeg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (repDeg != 0) {
            upload = fileSplitter.getChunkList();

            FileData file = new FileData(fileIDHashedStr, filePath);
            Peer.addFileToHandlerStored(file);

            startUploadChunks(fileIDHashedStr);
        }
        Peer.getSubprotocolInitManager().resetBackupInitiator();

        isBacking = false;
    }

    public void spaceReclaimStart(Chunk chunk, int repDeg, byte[] fileData) throws IOException{
        this.repDeg = repDeg;
        upload.add(chunk);
        this.fileData = fileData;

        System.out.println("AAAAAAAAAAAAAAAAA BBBBBBBBBBB: " + fileData);

        startUploadChunks(chunk.getFileID());

    }

    public String makeFileID() {
        String lastModified = Long.toString(new File(filePath).lastModified());

        String fileID = filePath + lastModified + Peer.getServerID() + repDeg;
        String fileIDHashedStr = CryptoHash.sha256(fileID);
        return fileIDHashedStr;
    }

    private void startUploadChunks(String fileIDHashedStr) throws IOException {
        int waitTime = 1;

        boolean replicationDone = false;
        do {
            if (numTransmissions > Utils.NUM_TRANSMISSIONS) {
                System.out.println("WARNING: number of transmissions exceeded.");
                return;
            }

            for (Chunk chunk : upload) {
                MessageHeader msgHeader = new MessageHeader(Message.MessageType.PUTCHUNK, protocol_version,
                        Peer.getServerID(), fileIDHashedStr, chunk.getChunkNum(), repDeg);
                MessageBody msgBody = new MessageBody(chunk.getChunkData());
                Message message = new Message(msgHeader, msgBody);
                byte[] buffer = message.getMessageBytes();
                Peer.sendMDBMessage(buffer);
            }

            try {
                TimeUnit.MILLISECONDS.sleep(1000 * waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int numChunksUploading = getNumChunksUploading();
            if (numChunksUploading == 0)
                replicationDone = true;
            else {
                numTransmissions++;
                waitTime *= 2;
            }
        } while (!replicationDone);

        upload.clear();
    }

    public void updateUploadingChunks(Message message) {
        if (isBacking) {
            System.out.println("UPDATE UPLOADING: " + Peer.getServerID());
            MessageHeader msgHeader = message.getHeader();

            int chunkNum = msgHeader.getChunkNum();
            int peerID = msgHeader.getPeerID();

            Chunk newChunk = new Chunk(chunkNum);
            for (Chunk chunk : upload) {
                if (chunk.equals(newChunk)) {
                    chunk.updateChunkRepDeg(peerID, chunk.getFinalRepDeg());
                    break;
                }
            }
        }

    }

    public void setSpaceReclaimCalled(boolean spaceReclaimCalled) {
        this.spaceReclaimCalled = spaceReclaimCalled;
        System.out.println("AAAAAAAAAAAAAAAKIIIIIIIIIIIIIII");
    }

    public int getNumChunksUploading() {
        int numChunksUploading = 0;

        Iterator<Chunk> it = upload.iterator();

        while (it.hasNext()) {
            Chunk chunk = it.next();
            System.out.println("CURR: " + chunk.getRepDeg() + "   " + "FINAL " + chunk.getFinalRepDeg());
            if (chunk.getRepDeg() == chunk.getFinalRepDeg())
                it.remove();
            else
                numChunksUploading++;
        }

        return numChunksUploading;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public int getRepDeg() {
        return repDeg;
    }

    public void setRepDeg(int repDeg) {
        this.repDeg = repDeg;
    }

    public int getnumTransmissions() {
        return numTransmissions;
    }

    public void setnumTransmissions(int numTransmissions) {
        this.numTransmissions = numTransmissions;
    }

    public ArrayList<Chunk> getUpload() {
        return upload;
    }

    public void setUpload(ArrayList<Chunk> upload) {
        this.upload = upload;
    }

    public void addToUpload(Chunk chunk) {
        this.upload.add(chunk);
    }

}