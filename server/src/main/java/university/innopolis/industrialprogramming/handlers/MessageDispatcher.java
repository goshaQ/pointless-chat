package university.innopolis.industrialprogramming.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import university.innopolis.industrialprogramming.ChatServer;
import university.innopolis.industrialprogramming.Client;
import university.innopolis.industrialprogramming.messages.MessageSerializer;
import university.innopolis.industrialprogramming.messages.Message;
import university.innopolis.industrialprogramming.messages.CommandMessage;
import university.innopolis.industrialprogramming.messages.TextMessage;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class MessageDispatcher {
    private static Logger logger = LogManager.getLogger(MessageDispatcher.class);

    private Map<Class, MessageHandler> messageHandlers = new HashMap<>();

    public MessageDispatcher(String path) {
        messageHandlers.put(TextMessage.class, new TextMessageHandler());
        messageHandlers.put(CommandMessage.class, new CommandMessageHandler(path));
    }


    public void dispatch(Client client, ByteBuffer byteBuffer, int bytes) {
        byteBuffer.position(0);

        int messageLength = byteBuffer.getInt();
        Message message = MessageSerializer.deserializeMessage(byteBuffer, messageLength);

        if (message instanceof TextMessage) {
            messageHandlers.get(TextMessage.class).handleMessage(client, message);
        } else if (message instanceof CommandMessage) {
            messageHandlers.get(CommandMessage.class).handleMessage(client, message);
        } else {
            logger.warn("Received message of unknown type!");
        }
    }
}
