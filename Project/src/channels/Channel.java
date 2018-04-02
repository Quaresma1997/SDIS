package channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public abstract class Channel implements Runnable{
    protected InetAddress ip;
    protected int port;
    protected MulticastSocket socket;
    /**
     * @param  ip
     * @param  port
     */
    public Channel (InetAddress ip, int port){
        this.ip = ip;
        this.port = port;

        initiateSocket();
    }
    /**
     * Starts the channel socket.
     */
    protected void initiateSocket() {
        try {
            socket = new MulticastSocket(port);
            socket.setTimeToLive(1);
            socket.joinGroup(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Closes the channel socket.
     */
    protected void closeSocket(){
        if(socket != null)
            socket.close();
    }
    /**
     * Sends the given message via this channel socket.
     * @param message
     */
    public void sendMessage(byte[] message) {
        DatagramPacket packet = new DatagramPacket(message, message.length, ip, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}