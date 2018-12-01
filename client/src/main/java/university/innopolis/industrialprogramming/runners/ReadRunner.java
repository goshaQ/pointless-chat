package university.innopolis.industrialprogramming.runners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import university.innopolis.industrialprogramming.messages.Message;
import university.innopolis.industrialprogramming.messages.MessageSerializer;
import university.innopolis.industrialprogramming.runners.notification.RunListener;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class ReadRunner implements IORunner {
    private static Logger logger = LogManager.getLogger(ReadRunner.class);
    private static boolean isRunning = true;

    private DataInputStream inputStream;
    private RunListener listener;

    public ReadRunner(RunListener listener, DataInputStream inputStream) {
        this.listener = listener;
        this.inputStream = inputStream;
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            Message message = readIncomingMessage();
            if (message != null) {
                printMessage(message);
            }
        }
    }

    private Message readIncomingMessage() {
        byte[] serializedMessage = read();

        ByteBuffer wrappedBytes = ByteBuffer.wrap(serializedMessage);
        wrappedBytes.position(0);

        int messageLength = wrappedBytes.getInt();
        return MessageSerializer.deserializeMessage(wrappedBytes, messageLength);
    }

    private void printMessage(Message message) {
        System.out.println(message);
        System.out.print("> ");
    }

    private byte[] read() {
        byte[] bytes = new byte[MessageSerializer.MAX_MESSAGE_LENGTH];
        try {
            int res = inputStream.read(bytes);
            if (res < 1) {
                throw new SocketException();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            listener.notifyThatIOExceptionHappened();
        }
        return bytes;
    }
}
