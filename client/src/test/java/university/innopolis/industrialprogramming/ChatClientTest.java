package university.innopolis.industrialprogramming;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import university.innopolis.industrialprogramming.messages.Message;
import university.innopolis.industrialprogramming.messages.MessageSerializer;
import university.innopolis.industrialprogramming.messages.TextMessage;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class ChatClientTest {
    private static final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private static final InputStream originalIn = System.in;
    private static final PrintStream originalOut = System.out;
    private static AsynchronousServerSocketChannel serverSocket;
    private static AsynchronousSocketChannel connectionSocket;
    private static Thread clientThread;

    @BeforeClass
    public static void before() throws IOException, InterruptedException, ExecutionException {
        System.setOut(new PrintStream(out));
        System.setIn(new ByteArrayInputStream((
                "User1" + System.lineSeparator() +
                        "/Command1" +  System.lineSeparator()).getBytes()));

        serverSocket = AsynchronousServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(7777));

        Future<AsynchronousSocketChannel> futureConnectionSocket = serverSocket.accept();

        String[] args = {"-host", "localhost", "-port", "7777"};
        clientThread = new Thread(() -> ChatClient.main(args));
        clientThread.start();

        Thread.sleep(1000);
        connectionSocket = futureConnectionSocket.get();
    }

    @AfterClass
    public static void after() throws IOException {
        System.setOut(originalOut);
        System.setIn(originalIn);

        serverSocket.close();
        connectionSocket.close();
        clientThread.interrupt();
    }

    @Test
    public void clientConnects() throws InterruptedException, ExecutionException {
        assertTrue(out.toString().contains("Connected to the host!"));

        Message message = readOutgoingMessage();

        assertEquals(message.getSender(), "User1");
        assertNull(message.getContent());
    }

    @Test
    public void clientReceivesMessages() throws ExecutionException, InterruptedException {
        Message message = new TextMessage("User2", "Message1");
        writeIncomingMessage(message);

        assertTrue(out.toString().contains(message.toString()));
    }

    private Message readOutgoingMessage() throws ExecutionException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(MessageSerializer.MAX_MESSAGE_LENGTH);
        connectionSocket.read(buffer).get();

        buffer.flip();

        int messageLength = buffer.getInt();
        return MessageSerializer.deserializeMessage(buffer, messageLength);
    }

    private void writeIncomingMessage(Message message) throws ExecutionException, InterruptedException {
        byte[] serializedMessage = MessageSerializer.serializeMessage(message);

        ByteBuffer output = ByteBuffer.allocate(Integer.BYTES + serializedMessage.length);
        output.putInt(serializedMessage.length);
        output.put(serializedMessage);
        output.flip();

        connectionSocket.write(output);
        Thread.sleep(1000);
    }
}