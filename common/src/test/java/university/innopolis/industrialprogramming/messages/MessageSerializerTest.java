package university.innopolis.industrialprogramming.messages;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class MessageSerializerTest {
    @Test
    public void properlySerializedMessage() {
        Message originalMessage = getDummyTextMessage();

        byte[] serializedMessage = MessageSerializer.serializeMessage(originalMessage);
        ByteBuffer output = ByteBuffer.wrap(serializedMessage);

        Message deserializedMessage = MessageSerializer.deserializeMessage(output, serializedMessage.length);

        assertEquals(originalMessage.getSender(), deserializedMessage.getSender());
        assertEquals(originalMessage.getContent(), deserializedMessage.getContent());
    }

    private Message getDummyTextMessage() {
        String sender = "Sender1";
        String content = "Message1";

        return new TextMessage(sender, content);
    }
}
