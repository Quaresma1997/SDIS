package files_data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import communication.Peer;
import utils.Utils;

public class FileData {
    private String filePath;
    private String fileID;
    private ArrayList<Chunk> chunkList = new ArrayList<>();

    public FileData(String fileID, String filePath) {
        this.fileID = fileID;
        this.filePath = filePath;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ArrayList<Chunk> getChunkList() {
        return chunkList;
    }

    public synchronized long getSize() {
        long size = 0;

        for (Chunk chunk : getChunksFromPeer(Peer.getServerID())) {
            String chunkPath = Utils.TMP_CHUNKS + Peer.getServerID() + '/' + fileID + chunk.getChunkNum();
            Path path = Paths.get(chunkPath);
            if (new File(chunkPath).exists()) {
                long chunkSize = 0;
                try {
                    chunkSize = Files.readAllBytes(path).length;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                size += chunkSize;
            }

        }

        return size;
    }

    public synchronized ArrayList<Chunk> getChunksGreaterRepDeg() {
        ArrayList<Chunk> retChunkList = new ArrayList<>();
        for (Chunk chunk : getChunksFromPeer(Peer.getServerID())) {
            if (chunk.getRepDegGreaterThanFinalRepDeg()) {
                retChunkList.add(chunk);
            }
        }
        return retChunkList;
    }

    public void setChunkList(ArrayList<Chunk> chunkList) {
        this.chunkList = chunkList;
    }

    public synchronized Chunk getChunk(int chunkID) {
        for (Chunk chunk : chunkList)
            if (chunk.getChunkNum() == chunkID)
                return chunk;

        return null;
    }

    public synchronized void addChunkStored(Chunk newChunk, int peerID) {
        for (Chunk chunk : chunkList)
            if (chunk.equals(newChunk)) {
                chunk.updateChunkRepDeg(peerID, -1);
                return;
            }

        newChunk.updateChunkRepDeg(peerID, -1);
        chunkList.add(newChunk);
    }

    public synchronized void addChunkPutchunk(Chunk newChunk, int peerID) {
        for (Chunk chunk : chunkList)
            if (chunk.equals(newChunk)) {
                chunk.updateChunkRepDeg(peerID, newChunk.getFinalRepDeg());
                chunk.setChunkData(newChunk.getChunkData());
                return;
            }

        newChunk.updateChunkRepDeg(peerID, newChunk.getFinalRepDeg());
        chunkList.add(newChunk);
    }

    public synchronized boolean removeChunk(int chunkID) {
        for (int i = 0; i < chunkList.size(); i++) {
            if (chunkList.get(i).getChunkNum() == chunkID) {
                chunkList.remove(i);
                return true;
            }
        }
        return false;
    }

    public synchronized ArrayList<Chunk> getChunksFromPeer(int peerID) {
        ArrayList<Chunk> chunksFromPeer = new ArrayList<>();
        for (Chunk chunk : chunkList) {
            if (chunk.checkChunkFromPeer(peerID)) {
                chunksFromPeer.add(chunk);
            }

        }
        return chunksFromPeer;
    }

    public synchronized void removeChunkFromPeer(int chunkNum, int peerID) {
        getChunk(chunkNum).removePeerID(peerID);
        chunkList.remove(getChunk(chunkNum));
        // System.out.println("CHUNK REMOVED " + chunkNum);
    }
}