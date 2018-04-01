package files_data;

import java.util.ArrayList;

import java.util.Collections;

public class Chunk implements Comparable<Chunk> {
    private String fileID;
    private int chunkNum;
    private byte[] chunkData = null;
    private int finalRepDeg;
    private int repDeg = 0;
    private ArrayList<Integer> peersIDs = new ArrayList<>();

    public Chunk(String fileID, int chunkNum, byte[] chunkData, int finalRepDeg) {
        this.finalRepDeg = finalRepDeg;
        this.fileID = fileID;
        this.chunkNum = chunkNum;
        this.chunkData = chunkData;
    }

    public Chunk(int chunkNum, byte[] chunkData) {
        this.chunkNum = chunkNum;
        this.chunkData = chunkData;
    }

    public Chunk(String fileID, int chunkNum) {
        this.fileID = fileID;
        this.chunkNum = chunkNum;
        this.finalRepDeg = 0;
    }

    public Chunk(int chunkNum) {
        this.chunkNum = chunkNum;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Chunk) {
            Chunk chunk = (Chunk) obj;
            return (chunk.getChunkNum() == chunkNum);
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Chunk chunk) {
        if (chunkNum > chunk.getChunkNum())
            return 1;
        else
            return 0;
    }

    public String getFileID() {
        return fileID;
    }

    public boolean getRepDegGreaterThanFinalRepDeg() {
        if (repDeg > finalRepDeg)
            return true;
        else
            return false;
    }

    public int getChunkNum() {
        return chunkNum;
    }

    public void setChunkNum(int chunkNum) {
        this.chunkNum = chunkNum;
    }

    public byte[] getChunkData() {
        return chunkData;
    }

    public void setChunkData(byte[] chunkData) {
        this.chunkData = chunkData;
    }

    public void updateChunkRepDeg(int peerID, int incFinal) {
        if (!checkChunkFromPeer(peerID)) {
            if (incFinal != -1)
                finalRepDeg = incFinal;
            peersIDs.add(peerID);
            incRepDeg();
        }
    }

    public boolean checkChunkFromPeer(int peerID) {
        for (int i = 0; i < peersIDs.size(); i++) {
            if (peersIDs.get(i) == peerID)
                return true;
        }

        return false;
    }

    public void removePeerID(int peerID) {
        for (int i = 0; i < peersIDs.size(); i++) {
            if (peersIDs.get(i) == peerID) {
                peersIDs.remove(i);
                decRepDeg();
                break;
            }
        }
    }

    public int getFinalRepDeg() {
        return finalRepDeg;
    }

    public void setFinalRepDeg(int finalRepDeg) {
        this.finalRepDeg = finalRepDeg;
    }

    public int getRepDeg() {
        return repDeg;
    }

    public void setRepDeg(int repDeg) {
        this.repDeg = repDeg;
    }

    public ArrayList<Integer> getPeersIDs() {
        return peersIDs;
    }

    public void setPeersIDs(ArrayList<Integer> peersIDs) {
        this.peersIDs = peersIDs;
    }

    public void incRepDeg() {
        this.repDeg++;
    }

    public void decRepDeg() {
        this.repDeg--;
    }

}