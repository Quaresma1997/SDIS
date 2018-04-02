package channels;

import java.net.InetAddress;

import java.net.MulticastSocket;
import java.nio.charset.Charset;

import subprotocols.SubprotocolManager;

import java.net.DatagramPacket;

import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import communication.Peer;
import utils.Utils;
import java.io.IOException;

public class MC_channel extends Channel {

    public MC_channel(InetAddress ip, int port) {
        super(ip, port);
        System.out.println("Started MC");
    }

    @Override
    public void run() {
        boolean stop = false;
        while (!stop) {
            byte[] buffer = new byte[Utils.HEADER_SIZE];
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