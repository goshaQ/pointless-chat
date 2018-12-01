package university.innopolis.industrialprogramming;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import university.innopolis.industrialprogramming.messages.Message;
import university.innopolis.industrialprogramming.messages.MessageSerializer;
import university.innopolis.industrialprogramming.messages.TextMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientTest
{
    private static Client client;
    private static AsynchronousSocketChannel connectionSocket;

    @BeforeClass
    public static void before() throws IOException, ExecutionException, InterruptedException {
        int port = 7777;
        String host = "localhost";

        AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(port));

        Future<AsynchronousSocketChannel> futureConnectionSocket = serverSocket.accept();

        AsynchronousSocketChannel clientSocket = AsynchronousSocketChannel.open();
        clientSocket.connect(new InetSocketAddress(host, port));
        client = new Client(clientSocket);

        connectionSocket = futureConnectionSocket.get();
        serverSocket.close();
    }

    @AfterClass
    public static void after() throws IOException {
        connectionSocket.close();
        client.close();
    }

    @Test
    public void sendIncomingMessage() {
        Message originalMessage = getDummyTextMessage();
        ByteBuffer buffer = ByteBuffer.allocate(MessageSerializer.MAX_MESSAGE_LENGTH);

        client.appendIncomingMessage(originalMessage);
        connectionSocket.read(buffer);

        buffer.position(0);

        int messageLength = buffer.getInt();
        Message receivedMessage = MessageSerializer.deserializeMessage(buffer, messageLength);

        assertEquals(originalMessage.getSender(), receivedMessage.getSender());
        assertEquals(originalMessage.getContent(), receivedMessage.getContent());
    }

    @Test
    public void appendAndRetrieveOutgoingMessage() {
        Message originalMessage = getDummyTextMessage();

        client.appendOutgoingMessage(originalMessage);

        assertTrue(client.hasNextOutgoingMessage());

        Message retrievedMessage = client.nextOutgoingMessage();

        assertEquals(originalMessage.getSender(), retrievedMessage.getSender());
        assertEquals(retrievedMessage.getContent(), retrievedMessage.getContent());
    }

    private Message getDummyTextMessage() {
        String sender = "Sender1";
        String content = "Message1";

        return new TextMessage(sender, content);
    }
}
