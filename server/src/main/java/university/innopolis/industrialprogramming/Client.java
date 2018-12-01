package university.innopolis.industrialprogramming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import university.innopolis.industrialprogramming.messages.Message;
import university.innopolis.industrialprogramming.messages.MessageSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.LinkedList;
import java.util.Queue;

public class Client {
    private static Logger logger = LogManager.getLogger(Client.class);
    private String name;
    private boolean writing = false;
    private final AsynchronousSocketChannel clientSocket;
    private final Queue<Message> incomingMessageQueue = new LinkedList<>();
    private final Queue<Message> outgoingMessageQueue = new LinkedList<>();

    Client(AsynchronousSocketChannel clientSocket) {
        this.clientSocket = clientSocket;
    }

    void read(CompletionHandler<Integer, ? super ByteBuffer> completionHandler) {
        ByteBuffer input = ByteBuffer.allocate(MessageSerializer.MAX_MESSAGE_LENGTH);
        clientSocket.read(input, input, completionHandler);
    }

    public void appendIncomingMessage(Message message) {
        boolean thisThreadWrite = false;

        synchronized (incomingMessageQueue) {
            incomingMessageQueue.add(message);
            if (!writing) {
                writing = true;
                thisThreadWrite = true;
            }
        }

        if (thisThreadWrite) {
            writeNextIncomingMessage();
        }
    }

    private void writeNextIncomingMessage() {
        Message message;
        synchronized (incomingMessageQueue) {
            message = incomingMessageQueue.poll();
            if (message == null) {
                writing = false;
            }
        }

        if (writing) {
            writeMessage(message);
        }
    }

    private void writeMessage(Message message) {
        byte[] serializedMessage = MessageSerializer.serializeMessage(message);

        ByteBuffer output = ByteBuffer.allocate(Integer.BYTES + serializedMessage.length);
        output.putInt(serializedMessage.length);
        output.put(serializedMessage);
        output.flip();

        clientSocket.write(output, output, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (buffer.hasRemaining()) {
                    clientSocket.write(buffer, buffer, this);
                } else {
                    writeNextIncomingMessage();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                logger.error(exc.getMessage(), exc);
            }
        });
    }

    public void appendOutgoingMessage(Message message) {
        synchronized (outgoingMessageQueue) {
            outgoingMessageQueue.add(message);
        }
    }

    Message nextOutgoingMessage() {
        synchronized (outgoingMessageQueue) {
            return outgoingMessageQueue.poll();
        }
    }

    boolean hasNextOutgoingMessage() {
        return !outgoingMessageQueue.isEmpty();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    void close() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
