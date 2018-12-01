package university.innopolis.industrialprogramming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import university.innopolis.industrialprogramming.runners.IORunner;
import university.innopolis.industrialprogramming.runners.ReadRunner;
import university.innopolis.industrialprogramming.runners.WriteRunner;
import university.innopolis.industrialprogramming.runners.notification.RunListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class ChatClient {
    private static Logger logger = LogManager.getLogger(ChatClient.class);

    private RunListener notifier = new RunListener();
    private List<IORunner> runners = new LinkedList<>();

    private ChatClient(String host, int port) {
        try (
                Socket clientSocket = new Socket(host, port);
                DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
        ) {
            System.out.println("Connected to the host!\n");
            proceed(inputStream, outputStream);
        } catch (IOException e) {
            logger.fatal(e.getMessage(), e);
        }
    }

    private void proceed(DataInputStream inputStream, DataOutputStream outputStream) {
        IORunner runner;
        notifier.register(this);

        runner = new WriteRunner(notifier, outputStream);
        runners.add(runner);

        Thread sendMessage = new Thread(runner);

        runner = new ReadRunner(notifier, inputStream);
        runners.add(runner);

        Thread readMessage = new Thread(runner);

        sendMessage.start();
        readMessage.start();

        try {
            sendMessage.join();
            readMessage.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(e.getMessage(), e);
        }
    }

    public void shutdown(int status) {
        for (IORunner runner : runners) {
            runner.stop();
        }
        System.exit(status);
    }

    private static void printUsage() {
        System.out.println("ChatClient [-host localhost] [-port <port number>]");
        System.exit(1);
    }

    public static void main(String[] args)
    {
        int port = -1;
        String host = "";

        if (args.length == 4) {
            try {
                if (args[0].equals("-host")) {
                    host = args[1];
                } else {
                    printUsage();
                }

                if (args[2].equals("-port")) {
                    port = Integer.parseInt(args[3]);
                } else {
                    printUsage();
                }
            } catch (NumberFormatException e) {
                printUsage();
            }
        } else {
            printUsage();
        }

        System.out.println("\nClient is connecting to the host \"" + host + "\", on port " + port + "!");
        new ChatClient(host, port);
    }
}
