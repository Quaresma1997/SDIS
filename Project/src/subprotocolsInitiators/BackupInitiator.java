package subprotocolsInitiators;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    public void spaceReclaimStart(Chunk chunk, int repDeg, byte[] fileData) throws IOException {
        this.repDeg = repDeg;
        upload.add(chunk);
        this.fileData = fileData;

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
                System.out.println("Number of transmissions over the max!");
                return;
            }

            for (Chunk chunk : upload) {
                MessageHeader msgHeader = new MessageHeader(Message.MessageType.PUTCHUNK, protocol_version,
                        Peer.getServerID(), fileIDHashedStr, chunk.getChunkNum(), repDeg);
                MessageBody msgBody = new MessageBody(chunk.getChunkData());
                Message message = new Message(msgHeader, msgBody);
                byte[] buffer = message.getMsgBytes();
                Peer.getMdbChannel().sendMessage(buffer);
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
        }while(!replicationDone);

        upload.clear();
    }

    public void updateUploadingChunks(Message message) {
        if (isBacking) {
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

    public int getNumChunksUploading() {
        int numChunksUploading = 0;
        for (int i = 0; i < upload.size(); i++) {
            if (upload.get(i).getRepDegGreaterEqualFinal()){
                // System.out.println("Chunk stored in other peer " + upload.get(i).getChunkNum() + "REP " +
                //  upload.get(i).getRepDeg() + " Final " + upload.get(i).getFinalRepDeg());
                upload.remove(i);
            }
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