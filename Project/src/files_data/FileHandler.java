package files_data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

import communication.Peer;
import message.Message;
import message.MessageHeader;
import utils.Utils;

public class FileHandler {
    private Map<String, FileData> stored = new HashMap<String, FileData>();

    private ArrayList<Chunk> backingUp = new ArrayList<Chunk>();
    private ArrayList<Chunk> restoringChunks = new ArrayList<Chunk>();

    public FileHandler() {
    }

    public Map<String, FileData> getStored() {
        return stored;
    }

    public void setStored(Map<String, FileData> stored) {
        this.stored = stored;
    }

    public FileData getFileFromFileID(String fileID) {
        return stored.get(fileID);
    }

    public FileData getFileFromFilePath(String filePath) {
        Map<String, FileData> aux_stored = stored;

        for (FileData file : aux_stored.values()) {

            if (file.getFilePath().equals(filePath))
                return file;

        }

        return null;
    }

    public void clearStored() {
        stored.clear();
    }

    public synchronized boolean addFileStored(FileData file) {

        if (stored.put(file.getFileID(), file) == null)
            return false;
        else
            return true;
    }

    public synchronized void addFileChunkPutchunk(Chunk chunk, String fileID) {
        addFileDataStored(fileID);
        stored.get(fileID).addChunkPutchunk(chunk, Peer.getServerID());
    }

    private synchronized void addFileDataStored(String fileID) {
        if (!stored.containsKey(fileID)) {
            FileData file = new FileData(fileID, "");
            stored.put(fileID, file);
        }
    }

    public boolean checkChunkStoredFromPeer(Chunk chunk, String fileID, int peerID) {
        if (!stored.containsKey(fileID)) {
            return false;
        }
        for (Chunk newChunk : stored.get(fileID).getChunksFromPeer(peerID)) {
            if (newChunk.equals(chunk))
                return true;
        }

        return false;
    }

    public ArrayList<Chunk> getBackingUpChunks() {
        return backingUp;
    }

    public void addBackingUpChunk(Chunk chunk) {
        backingUp.add(chunk);
    }

    public void removeBackingUpChunk(Chunk chunk) {
        backingUp.remove(chunk);
    }

    public void setBackingUpChunks(ArrayList<Chunk> backingUp) {
        this.backingUp = backingUp;
    }

    public ArrayList<Chunk> getRestoringChunks() {
        return restoringChunks;
    }

    public synchronized ArrayList<Chunk> getChunks() {
        ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
        Collection<FileData> files = stored.values();
        for (FileData file : files) {
            for (int i = 0; i < file.getChunkList().size(); i++)
                chunkList.add(file.getChunkList().get(i));
        }
        return chunkList;
    }

    public synchronized ArrayList<Chunk> getChunksFromPeer() {
        ArrayList<Chunk> chunkList = new ArrayList<Chunk>();
        Collection<FileData> files = stored.values();
        for (FileData file : files) {
            chunkList.addAll(file.getChunksFromPeer(Peer.getServerID()));
        }
        return chunkList;
    }

    public synchronized ArrayList<Chunk> getOrderedByRepDegChunks() {
        ArrayList<Chunk> chunkList = getChunks();
        chunkList.sort(Comparator.comparingInt(Chunk::getRepDeg));
        return chunkList;
    }

    public synchronized Chunk getStoredChunk(String fileID, int chunkNum) {
        FileData file = stored.get(fileID);
        Chunk chunk = null;
        if (file != null)
            chunk = file.getChunk(chunkNum);

        return chunk;
    }

    public synchronized void updateStoredChunks(Message message) {
        MessageHeader msgHeader = message.getHeader();

        int chunkNum = msgHeader.getChunkNum();
        int peerID = msgHeader.getPeerID();
        String fileID = msgHeader.getFileID();

        FileData newFile = new FileData(fileID, "");
        if (!stored.containsKey(fileID))
            addFileStored(newFile);
        FileData file = getFileFromFileID(fileID);
        Chunk chunk = new Chunk(fileID, chunkNum);
        file.addChunkStored(chunk, peerID);

    }

    public synchronized void deleteStoredFileChunks(String fileID) throws IOException {
        FileData file = getFileFromFileID(fileID);
        if (file == null)
            return;

        for (Chunk chunk : file.getChunkList()) {
            String chunkPath = Utils.TMP_CHUNKS + Peer.getServerID() + '/' + fileID + chunk.getChunkNum();
            if (chunk.checkChunkFromPeer(Peer.getServerID())) {
                Path path = Paths.get(chunkPath);
                Files.delete(path);
            }

        }

        stored.remove(fileID);

    }

    public synchronized long deleteStoredChunk(String fileID, int chunkNum, int peerID) throws IOException {
        FileData file = getFileFromFileID(fileID);
        long deletedSpace = 0;
        if (file == null)
            return deletedSpace;

        if (file.getChunk(chunkNum).checkChunkFromPeer(peerID)) {
            Path path = Paths.get(Utils.TMP_CHUNKS + peerID + '/' + fileID + chunkNum);
            deletedSpace = Files.readAllBytes(path).length;
            Files.delete(path);
            file.removeChunkFromPeer(chunkNum, peerID);
        }

        return deletedSpace;
    }

    public synchronized boolean removeChunksGreaterRepDeg(long spaceRequired) {
        long spaceRemove = getSpaceRemovable() - spaceRequired;
        System.out.println("SPACE: " + spaceRequired);

        if (spaceRemove > 0) {
            long removedSpace = 0;
            for (Chunk chunk : getChunksThatHaveGreaterRepDeg()) {
                long deletedSpaceChunk = 0;
                try {
                    deletedSpaceChunk = deleteStoredChunk(chunk.getFileID(), chunk.getChunkNum(), Peer.getServerID());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                removedSpace += deletedSpaceChunk;
                if (removedSpace >= spaceRemove)
                    break;
            }
        } else
            return false;

        return true;
    }

    public synchronized long getSpaceRemovable() {
        long space = 0;
        for (Chunk chunk : getChunksThatHaveGreaterRepDeg()) {
            Path path = Paths
                    .get(Utils.TMP_CHUNKS + Peer.getServerID() + '/' + chunk.getFileID() + chunk.getChunkNum());
            long chunkSize = 0;
            try {
                chunkSize = Files.readAllBytes(path).length;
            } catch (IOException e) {
                e.printStackTrace();
            }
            space += chunkSize;
        }

        return space;
    }

    public synchronized ArrayList<Chunk> getChunksThatHaveGreaterRepDeg() {
        ArrayList<Chunk> chunkList = new ArrayList<>();
        Collection<FileData> values = stored.values();
        for (FileData file : values) {
            ArrayList<Chunk> auxChunkList = file.getChunksGreaterRepDeg();
            if (auxChunkList != null && auxChunkList.size() != 0)
                chunkList.addAll(auxChunkList);
        }

        return chunkList;
    }

    public synchronized long getStoredUsedSpace() throws IOException {
        long space = 0;
        Collection<FileData> values = stored.values();
        for (FileData file : values) {
            space += file.getSize();
        }

        return space;
    }
}