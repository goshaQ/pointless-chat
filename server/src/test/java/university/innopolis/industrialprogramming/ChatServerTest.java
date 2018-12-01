package university.innopolis.industrialprogramming;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

import static org.junit.Assert.assertNotNull;

public class ChatServerTest {
    private static AsynchronousSocketChannel connectionSocket;
    private static Thread serverThread;

    @BeforeClass
    public static void before() throws IOException, InterruptedException {
        int port = 7778;
        String host = "localhost";
        String[] args = {"-port", "7778", "-lp", "../plugins/target"};

        serverThread = new Thread(() -> ChatServer.main(args));
        serverThread.start();

        Thread.sleep(1000);
        connectionSocket = AsynchronousSocketChannel.open();
        connectionSocket.connect(new InetSocketAddress(host, port));
    }

    @AfterClass
    public static void after() throws IOException {
        connectionSocket.close();
        serverThread.interrupt();
    }

    @Test
    public void serverReachable() throws IOException {
        assertNotNull(connectionSocket.getRemoteAddress());
    }
}
