package message;

import utils.Utils;

public class MessageHeader{
    private Message.MessageType messageType;
    private String protocol_version;
    private int peerID;
    private String fileID;
    private int chunkNum = -1;
    private int repDeg = -1;

    // Putchunk
    public MessageHeader(Message.MessageType messageType, String protocol_version, int peerID, String fileID, int chunkNum,
            int repDeg) {
        this.messageType = messageType;
        this.protocol_version = protocol_version;
        this.peerID = peerID;
        this.fileID = fileID;
        this.chunkNum = chunkNum;
        this.repDeg = repDeg;
    }

    // Stored, GetChunk, Chunk, Removed
    public MessageHeader(Message.MessageType messageType, String protocol_version, int peerID, String fileID, int chunkNum) {
        this.messageType = messageType;
        this.protocol_version = protocol_version;
        this.peerID = peerID;
        this.fileID = fileID;
        this.chunkNum = chunkNum;
    }

    // Delete
    public MessageHeader(Message.MessageType messageType, String protocol_version, int peerID, String fileID) {
        this.messageType = messageType;
        this.protocol_version = protocol_version;
        this.peerID = peerID;
        this.fileID = fileID;
    }

    public Message.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(Message.MessageType messageType) {
        this.messageType = messageType;
    }

    public String getProtocolVersion() {
        return protocol_version;
    }

    public void setProtocolVersion(String protocol_version) {
        this.protocol_version = protocol_version;
    }

    public int getPeerID() {
        return peerID;
    }

    public void setPeerID(int peerID) {
        this.peerID = peerID;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public int getChunkNum() {
        return chunkNum;
    }

    public void setChunkNum(int chunkNum) {
        this.chunkNum = chunkNum;
    }

    public int getRepDeg() {
        return repDeg;
    }

    public void setRepDeg(int repDeg) {
        this.repDeg = repDeg;
    }

    public String getMessageHeaderStr(){
        String msg;
        switch(messageType){
            case PUTCHUNK:
                msg = messageType + " " + protocol_version + " " + peerID + " " + fileID + " " + chunkNum + " " + repDeg
                    + " " + Utils.CRLF + Utils.CRLF;
                break;
            case DELETE:
                msg = messageType + " " + protocol_version + " " + peerID + " " + fileID + " " + Utils.CRLF + Utils.CRLF;
                break;
            default:
                msg = messageType + " " + protocol_version + " " + peerID + " " + fileID + " " + chunkNum + " " + Utils.CRLF
                    + Utils.CRLF;
                break;
        }

        return msg;
    }
}