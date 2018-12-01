package university.innopolis.industrialprogramming.runners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import university.innopolis.industrialprogramming.messages.CommandMessage;
import university.innopolis.industrialprogramming.messages.Message;
import university.innopolis.industrialprogramming.messages.MessageSerializer;
import university.innopolis.industrialprogramming.messages.TextMessage;
import university.innopolis.industrialprogramming.runners.notification.RunListener;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WriteRunner implements IORunner {
    private static Logger logger = LogManager.getLogger(WriteRunner.class);
    private static boolean isRunning = true;

    private DataOutputStream outputStream;
    private RunListener listener;
    private Scanner scanner = new Scanner(System.in);

    public WriteRunner(RunListener listener, DataOutputStream outputStream) {
        this.listener = listener;
        this.outputStream = outputStream;
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public void run() {
        justJoined();

        while (isRunning) {
            Message message = readOutgoingMessage();
            if (message != null) {
                writeMessage(message);
            }
        }
    }

    private void justJoined() {
        System.out.println("Please, enter your nickname: ");
        String str = scanner.nextLine();
        System.out.println();

        Message message = new TextMessage(str, null);
        writeMessage(message);
    }

    private Message readOutgoingMessage() {
        System.out.print("> ");
        String str = scanner.nextLine();

        if (str.equals("exit")) {
            listener.notifyExit();
            return null;
        }

        if (str.startsWith("/")) {
            Map<String, String> map = new HashMap<>();
            String[] args = str.split(" (?=(([^\"]*\"){2})*[^\"]*$)");

            map.put("command", args[0]);
            for (int i = 2; i < args.length; i++) {
                map.put(args[i - 1], args[i]);
            }

            return new CommandMessage(null, map);
        } else {
            return new TextMessage(null, str);
        }
    }

    private void writeMessage(Message message) {
        byte[] serializedMessage = MessageSerializer.serializeMessage(message);

        ByteBuffer output = ByteBuffer.allocate(Integer.BYTES + serializedMessage.length);
        output.putInt(serializedMessage.length);
        output.put(serializedMessage);
        output.flip();

        write(output.array());
    }

    private void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            listener.notifyThatIOExceptionHappened();
        }
    }
}
