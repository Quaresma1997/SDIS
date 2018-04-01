package subprotocols;

import java.io.IOException;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import communication.Peer;
import message.Message;
import utils.Utils;

public class SubprotocolManager implements Runnable {
    private AtomicBoolean toRun;
    private ExecutorService executor;
    private ConcurrentLinkedQueue<Message> messages;

    public SubprotocolManager() {
        messages = new ConcurrentLinkedQueue<>();
        toRun = new AtomicBoolean(true);
        executor = Executors.newFixedThreadPool(Utils.NUM_THREADS_POOL);

    }

    @Override
    public void run() {
        try {
            while (toRun.get() || !executor.isTerminated()) {
                Message message;
                while ((message = messages.poll()) != null) {
                    makeRequest(message);
                }
                // Sleep in case there wasn't any runnable in the queue. This helps to avoid hogging the CPU.
                Thread.sleep(1);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMessagesQueue(ConcurrentLinkedQueue<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(String newMsg) {
        Message message = new Message(newMsg);
        messages.add(message);
    }

    public void makeRequest(Message message) throws IOException {
      
        if(message.getHeader().getPeerID() == Peer.getServerID())
            return;

        System.out.println("MAKE REQUEST");
        System.out.println(message.getHeader().getMessageType());
        switch (message.getHeader().getMessageType()) {
        case PUTCHUNK:
            Backup backup = new Backup(message);
            executor.execute(backup);
            break;
        case STORED:
            Peer.updateFileStored(message);
            break;
        case GETCHUNK:
            Restore restore = new Restore(message);
            executor.execute(restore);
            break;
        case CHUNK:
            Peer.receiveChunk(message);
            break;
        case DELETE:
            Delete delete = new Delete(message);
            executor.execute(delete);
            break;
        case REMOVED:
            SpaceReclaim spaceReclaim = new SpaceReclaim(message);
            executor.execute(spaceReclaim);
            break;
        default:
            return;
        }
    }
}