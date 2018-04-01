package message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import utils.Utils;

public class Message {

    private MessageHeader msgHeader;
    private MessageBody msgBody;

    public enum MessageType {
        PUTCHUNK, STORED, GETCHUNK, CHUNK, DELETE, REMOVED
        // ENH_DELETED,
        // ENH_AWOKE
    }

    /**
    * Body and header
    */
    public Message(MessageHeader msgHeader, MessageBody msgBody) {
        this.msgBody = msgBody;
        this.msgHeader = msgHeader;
    }

    /**
     * Only header
     */
    public Message(MessageHeader msgHeader) {
        this.msgBody = null;
        this.msgHeader = msgHeader;
    }

    public Message(String message){
        String[] msgSplit = message.split("\\R\\R", 2);

        String newMsgHeader = "";
        String newMsgBody = "";


        if (msgSplit.length == 0 || msgSplit.length > 2) {
            return; //message discarded
        } else if (msgSplit.length == 2)
            newMsgBody = msgSplit[1];

        newMsgHeader = msgSplit[0];

        String[] headerSplit = newMsgHeader.split("\\s+");

        MessageType type;
        int numberOfArgs;

        switch (headerSplit[0]) {
        case "PUTCHUNK":
            type = MessageType.PUTCHUNK;
            numberOfArgs = 6;
            break;
        case "STORED":
            type = MessageType.STORED;
            numberOfArgs = 5;
            break;
        case "GETCHUNK":
            type = MessageType.GETCHUNK;
            numberOfArgs = 5;
            break;
        case "CHUNK":
            type = MessageType.CHUNK;
            numberOfArgs = 5;
            break;
        case "DELETE":
            type = MessageType.DELETE;
            numberOfArgs = 4;
            break;
        case "REMOVED":
            type = MessageType.REMOVED;
            numberOfArgs = 5;
            break;
        default:
            return;
        }

        if (headerSplit.length != numberOfArgs)
            return;

        String protocol_version;
        int peerID;
        String fileID;
        int chunkNum;
        int repDeg;

        protocol_version = headerSplit[1];
        peerID = Integer.parseInt(headerSplit[2]);
        fileID = headerSplit[3];

        //TODO: METER SWITCH

        if (type == MessageType.PUTCHUNK) {
            chunkNum = Integer.parseInt(headerSplit[4]);
            repDeg = Integer.parseInt(headerSplit[5]);
            msgHeader = new MessageHeader(type, protocol_version, peerID, fileID, chunkNum, repDeg);
        } else if (type == MessageType.GETCHUNK || type == MessageType.CHUNK
                || type == MessageType.REMOVED || type == MessageType.STORED) {
            chunkNum = Integer.parseInt(headerSplit[4]);
            msgHeader = new MessageHeader(type, protocol_version, peerID, fileID, chunkNum);
        } else
            msgHeader = new MessageHeader(type, protocol_version, peerID, fileID);

        if (newMsgBody != "") 
            msgBody = new MessageBody(newMsgBody.getBytes(Charset.forName("ISO_8859_1")));
        
    }

    public String getMessageString() {
        if (msgBody == null)
            return msgHeader.getMessageHeaderStr();
        else {
            String new_string = new String(msgBody.getData());
            return msgHeader.getMessageHeaderStr() + new_string;
        }
    }

    public byte[] getMessageBytes() throws IOException {
        byte header[] = msgHeader.getMessageHeaderStr().getBytes(Charset.forName("ISO_8859_1"));

        if (msgBody == null) 
            return header;               
        else{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(header);
            outputStream.write(msgBody.getData());
            return outputStream.toByteArray();
        }
    }

    public MessageBody getBody() {
        return msgBody;
    }

    public void setBody(MessageBody msgBody) {
        this.msgBody = msgBody;
    }

    public MessageHeader getHeader() {
        return msgHeader;
    }

    public void setHeader(MessageHeader msgHeader) {
        this.msgHeader = msgHeader;
    }
}