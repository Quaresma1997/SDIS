package subprotocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Backup implements Runnable{

    private File file;

    public Backup(File file) {
        this.file = file;
    }

    @Override
	public void run() {

    }
}