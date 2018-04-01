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
    // private String lastModified;

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

    public long getSize() throws IOException {
        long size = 0;

        for (Chunk chunk : chunkList) {
            if (chunk.checkChunkFromPeer(Peer.getServerID())) {
                String chunkPath = Utils.TMP_CHUNKS + Peer.getServerID() + '/' + fileID + chunk.getChunkNum();
                Path path = Paths.get(chunkPath);
                if (new File(chunkPath).exists()) {
                    long chunkSize = Files.readAllBytes(path).length;
                    size += chunkSize;
                }
            }
        }

        return size;
    }

    public ArrayList<Chunk> getChunksGreaterRepDeg() {
        ArrayList<Chunk> retChunkList = new ArrayList<>();
        for (Chunk chunk : chunkList) {
            if (chunk.checkChunkFromPeer(Peer.getServerID()) && chunk.getRepDegGreaterThanFinalRepDeg()) {
                retChunkList.add(chunk);
            }
        }
        return retChunkList;
    }

    public void setChunkList(ArrayList<Chunk> chunkList) {
        this.chunkList = chunkList;
    }

    public Chunk getChunk(int chunkID) {
        for (Chunk chunk : chunkList)
            if (chunk.getChunkNum() == chunkID)
                return chunk;

        return null;
    }

    public synchronized void addChunkStored(Chunk newChunk, int peerID) {
        System.out.println("STORED THE PEER: " + newChunk.getChunkNum() + "     " + peerID);
        for (Chunk chunk : chunkList)
            if (chunk.equals(newChunk)) {
                chunk.updateChunkRepDeg(peerID, -1);
                return;
            }

        newChunk.updateChunkRepDeg(peerID, -1);
        chunkList.add(newChunk);
    }

    public synchronized void addChunkPutchunk(Chunk newChunk, int peerID) {
        System.out.println("PUTCHUNK THE PEER: " + newChunk.getChunkNum() + "     " + peerID);
        for (Chunk chunk : chunkList)
            if (chunk.equals(newChunk)) {
                chunk.updateChunkRepDeg(peerID, newChunk.getFinalRepDeg());
                return;
            }

        newChunk.updateChunkRepDeg(peerID, newChunk.getFinalRepDeg());
        chunkList.add(newChunk);
    }

    public boolean removeChunk(int chunkID) {
        for (int i = 0; i < chunkList.size(); i++) {
            if (chunkList.get(i).getChunkNum() == chunkID) {
                chunkList.remove(i);
                return true;
            }
        }

        return false;
    }

    public boolean addPeerIDToChunk(int chunkNum, int peerID) {
        for (Chunk chunk : chunkList) {
            if (chunk.getChunkNum() == chunkNum) {
                chunk.updateChunkRepDeg(peerID, chunk.getFinalRepDeg());
                return true;
            }

        }

        return false;
    }

    public ArrayList<Chunk> getChunksFromPeer(int peerID) {
        ArrayList<Chunk> chunksFromPeer = new ArrayList<>();
        for (Chunk chunk : chunkList) {
            if (chunk.checkChunkFromPeer(peerID)) {
                chunksFromPeer.add(chunk);
            }

        }
        return chunksFromPeer;
    }

    public void removeChunkFromPeer(int chunkNum, int peerID) {
        for (int i = 0; i < chunkList.size(); i++) {
            if (chunkList.get(i).getChunkNum() == chunkNum) {
                chunkList.remove(i);
                break;
            }
        }
        /*  for (Chunk chunk : chunkList) {
            if (chunk.getChunkNum() == chunkNum) {
                chunk.removePeerID(peerID);
                chunk.decRepDeg();
            }
        
            if(chunk.getPeersIDs().size() == 0){
                chunkList.remove(chunk);
                break;
            } 
        } */

    }

    public String toString() {
        return "fileID: " + fileID + " filepath: " + filePath + " numChunks: " + chunkList.size();
    }
}