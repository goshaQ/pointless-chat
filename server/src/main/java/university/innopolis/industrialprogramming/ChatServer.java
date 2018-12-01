package university.innopolis.industrialprogramming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import university.innopolis.industrialprogramming.handlers.MessageDispatcher;
import university.innopolis.industrialprogramming.messages.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatServer {
    private static Logger logger = LogManager.getLogger(ChatServer.class);
    private static String librariesPath;

    private final List<Client> clients = new LinkedList<>();
    private final AsynchronousServerSocketChannel serverSocket;
    private final MessageDispatcher messageDispatcher = new MessageDispatcher(librariesPath);
    private boolean isListening = true;

    private ChatServer(int port) {
        AsynchronousServerSocketChannel socket = null;
        try {
            socket = AsynchronousServerSocketChannel.open();
            socket.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            logger.fatal(e.getMessage(), e);
            return;
        } finally {
            serverSocket = socket;
        }

        acceptConnection();

        try {
            Thread.sleep(TimeUnit.HOURS.toMillis(1));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(e.getMessage(), e);
        }
    }

    private void acceptConnection() {
        System.out.println("Listening for incoming connections...\n");

        serverSocket.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Void attachment) {
                if (!isListening) return;

                serverSocket.accept(null, this);
                handleConnection(result);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                logger.error(exc.getMessage(), exc);
            }
        });
    }

    private void handleConnection(AsynchronousSocketChannel clientSocket) {
        Client client = new Client(clientSocket);
        synchronized (clients) {
            clients.add(client);
        }

        ClientReader clientReader = new ClientReader(this, messageDispatcher);
        clientReader.run(client);
    }

    void writeMessageToEveryoneElse(Client client, Message message) {
        synchronized (clients) {
            for (Client c : clients) {
                if (c != client) {
                    c.appendIncomingMessage(message);
                }
            }
        }
    }

    void removeClient(Client client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    private static void printUsage() {
        System.out.println("ChatServer [-port <port number>] [-lp <path to the libs>]");
        System.exit(1);
    }

    public static void main(String[] args)
    {
        int port = -1;
        if (args.length == 4) {
            try {
                if (args[0].equals("-port")) {
                    port = Integer.parseInt(args[1]);
                } else {
                    printUsage();
                }
                if (args[2].equals("-lp")) {
                    librariesPath = args[3];
                } else {
                    printUsage();
                }
            } catch (NumberFormatException e) {
                printUsage();
            }
        } else {
            printUsage();
        }

        System.out.println("\nServer is running on port " + port + "!");
        new ChatServer(port);
    }
}
