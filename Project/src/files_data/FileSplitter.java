package files_data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import utils.Utils;

import java.nio.file.Path;

import static java.util.Arrays.copyOfRange;

public class FileSplitter {
    private ArrayList<Chunk> chunkList = new ArrayList<>();
    private byte[] fileData;

    public FileSplitter(byte[] fileData) {
        this.fileData = fileData;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public ArrayList<Chunk> getChunkList() {
        return chunkList;
    }

    public void setChunkList(ArrayList<Chunk> chunkList) {
        this.chunkList = chunkList;
    }

    public void splitFileIntoChunks(String fileID, int repDeg) throws IOException {
        int fileSize = fileData.length;
        int chunkNum = 1;

        if (fileSize <= Utils.BODY_SIZE) {
            Chunk chunk = new Chunk(fileID, chunkNum, fileData, repDeg);
            chunkList.add(chunk);
            if (fileSize == Utils.BODY_SIZE) {
                chunkNum++;
                Chunk emptyChunk = new Chunk(fileID, chunkNum, new byte[0], repDeg);
                chunkList.add(emptyChunk);
            }
        } else {
            for (int i = Utils.BODY_SIZE; i <= fileSize; i += Utils.BODY_SIZE) {
                byte[] chunkData = copyOfRange(fileData, i - Utils.BODY_SIZE, i);
                Chunk chunk = new Chunk(fileID, chunkNum, chunkData, repDeg);
                chunkList.add(chunk);
                chunkNum++;

            }

            int dataLeft = fileSize - ((chunkNum - 1) * Utils.BODY_SIZE);
            if (dataLeft != 0) {
                byte[] chunkData = copyOfRange(fileData, fileSize - dataLeft, fileSize);
                Chunk chunk = new Chunk(fileID, chunkNum, chunkData, repDeg);
                chunkList.add(chunk);
            }else{
                Chunk emptyChunk = new Chunk(fileID, chunkNum, new byte[0], repDeg);
                chunkList.add(emptyChunk);
            }
        }
    }
}