package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public abstract class Channel implements Runnable{
    protected InetAddress ip;
    protected int port;
    protected MulticastSocket socket;

    public Channel (InetAddress ip, int port){
        this.ip = ip;
        this.port = port;

        initiateSocket();
    }

    protected void initiateSocket() {
        try {
            socket = new MulticastSocket(port);
            socket.setTimeToLive(1);
            socket.joinGroup(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void closeSocket(){
        if(socket != null)
            socket.close();
    }

    public int getPort(){
        return port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public InetAddress getIP() {
        return ip;
    }

    public void setIP(InetAddress ip) {
        this.ip = ip;
    }

    public MulticastSocket getSocket() {
        return socket;
    }

    public void setSocket(MulticastSocket socket) {
        this.socket = socket;
    }

    public void sendMessage(byte[] message) {
        DatagramPacket packet = new DatagramPacket(message, message.length, ip, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}