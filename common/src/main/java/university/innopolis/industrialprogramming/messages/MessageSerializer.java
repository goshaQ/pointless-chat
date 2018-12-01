package university.innopolis.industrialprogramming.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class MessageSerializer {
    private static Logger logger = LogManager.getLogger(MessageSerializer.class);

    public static final int MAX_MESSAGE_LENGTH = 4096;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private MessageSerializer() {}

    public static byte[] serializeMessage(Message message) {
        byte[] buff = null;
        try {
            buff = objectMapper.writeValueAsBytes(message);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }

        return buff;
    }

    public static Message deserializeMessage(ByteBuffer byteBuffer, int bytes) {
        Message message = null;
        try {
            message = objectMapper.readValue(byteBuffer.array(), byteBuffer.position(), bytes, Message.class);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);

        }

        return message;
    }
}
