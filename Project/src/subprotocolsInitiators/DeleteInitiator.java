package subprotocolsInitiators;

import java.io.IOException;

import communication.Peer;
import files_data.FileData;
import message.*;
import utils.Utils;

public class DeleteInitiator extends SubprotocolInitiator {
    private String filePath;

    public DeleteInitiator(String protocol_version) throws IOException {
        super(protocol_version);

    }

    @Override
    public void initiate() throws IOException {
        FileData file = Peer.getFileFromHandlerStored(filePath);

        if (file == null) {
            System.out.println("File has not started backup in this peer.");
            return;
        }

        String fileID = file.getFileID();

        MessageHeader msgHeader = new MessageHeader(Message.MessageType.DELETE, protocol_version, Peer.getServerID(),
                fileID);
        Message message = new Message(msgHeader);
        byte[] buffer = message.getMsgBytes();

        Peer.deleteFileStored(fileID);

        //Send the delete message 3 times to ensure it gets deleted
        for (int i = 0; i < Utils.DELETE_MSGS_NUM; i++)
            Peer.getMcChannel().sendMessage(buffer);

        Peer.getSubprotocolInitManager().resetDeleteInitiator();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}