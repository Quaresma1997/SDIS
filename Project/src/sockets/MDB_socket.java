package sockets;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class MDB_socket implements Runnable {

    MulticastSocket socket;

    InetAddress ip;
    int port;

    public MDB_socket(int port, InetAddress ip) {
        this.port = port;
        this.ip = ip;
    }

    public void run() {
        try {
            socket = new MulticastSocket(port);

            socket.setTimeToLive(1);

            socket.joinGroup(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buf = new byte[64000];

        boolean stop = false;
        while (!stop) {
            try {
                DatagramPacket rpacket = new DatagramPacket(buf, buf.length);

                socket.receive(rpacket);
                s_ip = rpacket.getAddress();
                s_port = rpacket.getPort();
                

                //verificar se veio dele mesmo
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        socket.close();
    }

}