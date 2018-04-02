package message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import utils.Utils;

public class Message {

    private MessageHeader msgHeader;
    private MessageBody msgBody;

    String newMsgHeader = "";
    String newMsgBody = "";

    public enum MessageType {
        PUTCHUNK, STORED, GETCHUNK, CHUNK, DELETE, REMOVED
    }

    public Message(MessageHeader msgHeader, MessageBody msgBody) {
        this.msgBody = msgBody;
        this.msgHeader = msgHeader;
    }

    public Message(MessageHeader msgHeader) {
        this.msgBody = null;
        this.msgHeader = msgHeader;
    }

    public Message(String message) {

        String[] header = splitMessage(message);

        if (header == null) {
            System.out.println("Message received with bad header/body!");
            return;
        }

        createMessageParts(header);

    }

    private String[] splitMessage(String message) {
        String[] msg = message.split("\\R\\R", 2);

        if (msg.length == 0 || msg.length > 2) {
            return null;
        } else if (msg.length == 2)
            newMsgBody = msg[1];

        newMsgHeader = msg[0];

        return msg[0].split("\\s+");
    }

    private MessageType getType(String op) {
        MessageType type = null;
        switch (op) {
        case "PUTCHUNK":
            type = MessageType.PUTCHUNK;
            break;
        case "STORED":
            type = MessageType.STORED;
            break;
        case "GETCHUNK":
            type = MessageType.GETCHUNK;
            break;
        case "CHUNK":
            type = MessageType.CHUNK;
            break;
        case "DELETE":
            type = MessageType.DELETE;
            break;
        case "REMOVED":
            type = MessageType.REMOVED;
            break;
        default:
            return type;
        }

        return type;
    }

    private void createMessageParts(String[] header) {
        MessageType type = getType(header[0]);

        String protocol_version;
        int peerID;
        String fileID;
        int chunkNum;
        int repDeg;

        protocol_version = header[1];
        peerID = Integer.parseInt(header[2]);
        fileID = header[3];

        //TODO: METER SWITCH

        switch (type) {
        case PUTCHUNK:
            chunkNum = Integer.parseInt(header[4]);
            repDeg = Integer.parseInt(header[5]);
            msgHeader = new MessageHeader(type, protocol_version, peerID, fileID, chunkNum, repDeg);
            break;
        case GETCHUNK:
        case CHUNK:
        case STORED:
        case REMOVED:
            chunkNum = Integer.parseInt(header[4]);
            msgHeader = new MessageHeader(type, protocol_version, peerID, fileID, chunkNum);
            break;
        case DELETE:
            msgHeader = new MessageHeader(type, protocol_version, peerID, fileID);
            break;
        default:
            break;

        }

        if (newMsgBody != "")
            msgBody = new MessageBody(newMsgBody.getBytes(Charset.forName("ISO_8859_1")));
    }

    public byte[] getMsgBytes() throws IOException {
        byte header[] = msgHeader.getMessageHeaderStr().getBytes(Charset.forName("ISO_8859_1"));
        if (msgBody == null)
            return header;
        else {
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