package subprotocolsInitiators;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import communication.Peer;
import files_data.Chunk;
import files_data.FileData;
import utils.Utils;

public class StateInformationInitiator extends SubprotocolInitiator {
    private String str;

    public StateInformationInitiator(String protocol_version) throws IOException {
        super(protocol_version);
        str = "\n\n**************************************************************************"
                + "\n**************************************************************************"
                + "\n**************************************************************************\n\n";
        //        initiate();

    }

    @Override
    public void initiate() throws IOException {
        ArrayList<Chunk> peerChunks = new ArrayList<>();

        Map<String, FileData> stored = Peer.getFileHandler().getStored();
        for (Map.Entry<String, FileData> entry : stored.entrySet()) {
            String fileID = entry.getKey();
            FileData file = entry.getValue();
            if (file.getFilePath() != "") {
                int finRepDeg = 0;
                if (file.getChunkList().size() != 0) {
                    finRepDeg = file.getChunkList().get(0).get();
                }
                str += "File path: " + file.getFilePath() + ", fileID: " + fileID + ", final replication degree: "
                        + finRepDeg;

                for (Chunk chunk : file.getChunkList()) {
                    str += "\n\tChunkNum: " + chunk.getChunkNum() + ", current replication degree: "
                            + chunk.getRepDeg();
                    if (chunk.getPeersIDs().contains(Peer.getServerID()))
                        peerChunks.add(chunk);
                }

                str += "\n\n**************************************************************************"
                        + "\n**************************************************************************"
                        + "\n**************************************************************************\n\n";
            }
        }

        str += "Chunks stored in this peer";
        for (Chunk chunk : Peer.getFileHandler().getChunksFromPeer()) {
            Path path = Paths
                    .get(Utils.TMP_CHUNKS + Peer.getServerID() + '/' + chunk.getFileID() + chunk.getChunkNum());
            long chunkSize = Files.readAllBytes(path).length;

            str += "\n\tChunkNum: " + chunk.getChunkNum() + ", size: " + chunkSize + " bytes, replication degree: "
                    + chunk.getFinalRepDeg();
        }

        str += "\n\nPeer storage capacity: " + Peer.getSpaceReclaimed() + "KB, space occupied: "
                + Peer.getOcupiedSpace() + " Bytes (or " + Peer.getOcupiedSpace() / 1000.0 + " KBytes).\n";

        str += "\n\n**************************************************************************"
                + "\n**************************************************************************"
                + "\n**************************************************************************\n\n";

    }

    public String getMessage() {
        String ret = str;
        str = "\n\n**************************************************************************"
                + "\n**************************************************************************"
                + "\n**************************************************************************\n\n";
        return ret;
    }
}