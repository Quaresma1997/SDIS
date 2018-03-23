package files_data;

public class Chunk{
    private String fileID;
    private int chunkNo;
    private byte[] data;
    private int repDegree;
    private int chunkSize;

    public Chunk(int chunkNo, String fileID, byte[] data, int repDegree, int chunkSize) {
        this.repDegree = repDegree;
    	this.fileID = fileID;
    	this.chunkNo = chunkNo;
        this.data = data;
        this.chunkSize = chunkSize;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileID) {
        this.fileId = fileID;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public void setChunkNo(int chunkNo) {
        this.chunkNo = chunkNo;
    }

    public byte[] getData() {
        return chunkData;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getRepDegree() {
        return repDegree;
    }

    public void setRepDegree(int repDegree) {
        this.repDegree = repDegree;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        chunkSize = chunkSize;
    }

    public void incRepDegree(){
        this.repDegree++;
    }

    public void decRepDegree(){
        this.repDegree--;
    }

}