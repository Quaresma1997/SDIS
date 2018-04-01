package subprotocolsInitiators;

import communication.*;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class SubprotocolInitiator {
    protected String protocol_version;

    public SubprotocolInitiator(String protocol_version) {
        this.protocol_version = protocol_version;
    }

    public String getProtocolVersion() {
        return protocol_version;
    }

    public void setProtocolVersion(String protocol_version) {
        this.protocol_version = protocol_version;
    }

    public abstract void initiate() throws IOException, InterruptedException;

}