package message;

public class MessageHandler implements Runnable {

    public MessageHandler(DatagramPacket packet) {
		this.packet = packet;

		header = null;
		headerCodes = null;
		body = null;
	}

    public void run() {

    }
}