package channels;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.Charset;

import communication.Peer;
import subprotocols.SubprotocolManager;

import java.io.IOException;
import java.net.DatagramPacket;

import utils.Utils;

public class MDR_channel extends Channel {

    public MDR_channel(InetAddress ip, int port) {
        super(ip, port);
        System.out.println("Started MDR.");

    }

    @Override
    public void run() {
        boolean stop = false;
        while (!stop) {
            byte[] buffer = new byte[Utils.CHUNK_MAX_SIZE];
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(dp);
                String message = new String(dp.getData(), 0, dp.getLength(), Charset.forName("ISO_8859_1"));
                Peer.getSubprotocolManager().addMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeSocket();
    }

}