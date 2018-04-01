package subprotocolsInitiators;

import java.io.IOException;

import subprotocolsInitiators.*;

public class SubprotocolInitManager {
    private BackupInitiator backup;
    private DeleteInitiator delete;
    private RestoreInitiator restore;
    private SpaceReclaimInitiator space;
    private StateInformationInitiator state;
    private String protocol_version;

    public SubprotocolInitManager(String protocol_version) throws IOException, InterruptedException {
        this.protocol_version = protocol_version;
        backup = new BackupInitiator(protocol_version);
        delete = new DeleteInitiator(protocol_version);
        restore = new RestoreInitiator(protocol_version);
        space = new SpaceReclaimInitiator(protocol_version);
        state = new StateInformationInitiator(protocol_version);
    }

    public BackupInitiator getBackupInitiator(){
        return backup; 
    }

    public DeleteInitiator getDeleteInitiator(){
        return delete;
    }

    public RestoreInitiator getRestoreInitiator() {
        return restore;
    }

    public SpaceReclaimInitiator getSpaceReclaimInitiator() {
        return space;
    }

    public StateInformationInitiator getStateInformationInitiator() {
        return state;
    }

    public void resetBackupInitiator() throws IOException{         
        backup = new BackupInitiator(protocol_version);
    }

    public void resetDeleteInitiator() throws IOException {
        delete = new DeleteInitiator(protocol_version);
    }

    public void resetRestoreInitiator() throws IOException, InterruptedException {
        restore = new RestoreInitiator(protocol_version);
    }

    public void resetSpaceReclaimInitiator() throws IOException {
        space = new SpaceReclaimInitiator(protocol_version);
    }

    public void resetStateInformationInitiator() throws IOException {
        state = new StateInformationInitiator(protocol_version);
    }
}