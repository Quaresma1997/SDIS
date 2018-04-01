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

    /*public void resetSplitter() {
        fileData = null;
        chunks.clear();
    } */

    public void splitFileIntoChunks(String fileID, int repDeg) throws IOException {
        int fileSize = fileData.length;
        int chunkNum = 1;

        if (fileSize <= Utils.BODY_SIZE) {
            Chunk chunk = new Chunk(fileID, chunkNum, fileData, repDeg);
            chunkList.add(chunk);
            if (fileSize == Utils.BODY_SIZE) {
                chunkNum++;
                byte[] data_zero = new byte[0];
                Chunk emptyChunk = new Chunk(fileID, chunkNum, data_zero, repDeg);
                chunkList.add(emptyChunk);
            }
        } else {
            for (int i = Utils.BODY_SIZE; i <= fileSize; i += Utils.BODY_SIZE) {
                byte[] chunkData = copyOfRange(fileData, i - Utils.BODY_SIZE, i);
                Chunk chunk = new Chunk(fileID, chunkNum, chunkData, repDeg);
                //System.out.println(Arrays.toString(chunkData));
                chunkList.add(chunk);
                chunkNum++;

            }

            int dataLeft = fileSize - ((chunkNum - 1) * Utils.BODY_SIZE);
            if (dataLeft != 0) {
                System.out.println("DOUBT1: " + (fileSize - dataLeft) + "        " + fileSize);
                byte[] chunkData = copyOfRange(fileData, fileSize - dataLeft, fileSize);
                Chunk chunk = new Chunk(fileID, chunkNum, chunkData, repDeg);
                System.out.println("Splitter: size " + chunkData.length);
                //System.out.println(Arrays.toString(chunkData));
                chunkList.add(chunk);
            }else{
                byte[] data_zero = new byte[0];
                Chunk emptyChunk = new Chunk(fileID, chunkNum, data_zero, repDeg);
                chunkList.add(emptyChunk);
            }
        }
    }
}