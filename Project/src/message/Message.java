package messages;

public class Message implements Runnable {

    private MessageHeader msgHeader;
    private MessageBody msgBody;

	public enum MessageType {
        PUTCHUNK,
        STORED,
        GETCHUNK,
        CHUNK,
        DELETE,
        REMOVED
        // ENH_DELETED,
        // ENH_AWOKE
    }

    public Message(MessageBody msgBody, MessageHeader msgHeader){
        this.msgBody = msgBody;
        this.msgHeader = msgHeader;
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