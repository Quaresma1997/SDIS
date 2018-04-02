package subprotocols;

import java.io.IOException;

import communication.Peer;
import message.Message;
import message.MessageHeader;
import message.*;

public class Delete implements Runnable {
    private MessageHeader msgHeader;

    public Delete(Message message) {
        this.msgHeader = message.getHeader();
        System.out.println("Delete started");
    }

    @Override
    public void run() {
        String fileID = msgHeader.getFileID();
        try {
            Peer.deleteFileStored(fileID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}