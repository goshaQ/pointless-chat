package university.innopolis.industrialprogramming.handlers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import university.innopolis.industrialprogramming.Client;
import university.innopolis.industrialprogramming.messages.CommandMessage;
import university.innopolis.industrialprogramming.messages.Message;
import university.innopolis.industrialprogramming.messages.MessageSerializer;
import university.innopolis.industrialprogramming.messages.TextMessage;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class MessageDispatcherTest {
    private static MessageDispatcher messageDispatcher = new MessageDispatcher("../plugins/target");

    @Mock
    private Client client;

    @Test
    public void properlyHandledTextMessage() {
        Message message = getDummyTextMessage();
        ByteBuffer output = serializeMessage(message);


        Mockito.when(client.getName()).thenReturn(message.getSender());

        messageDispatcher.dispatch(client, output, -1);

        Mockito.verify(client).appendOutgoingMessage(any(Message.class));
    }

    @Test
    public void properlyHandledCommandMessage() throws InterruptedException {
        Message message = getDummyCommandMessage();
        ByteBuffer output = serializeMessage(message);

        messageDispatcher.dispatch(client, output, -1);

        // ToDo: Probably there should be a better way to await termination
        Thread.sleep(1000);
        Mockito.verify(client).appendIncomingMessage(any(Message.class));
    }

    private Message getDummyTextMessage() {
        String sender = "Sender1";
        String content = "Message1";

        return new TextMessage(sender, content);
    }

    private Message getDummyCommandMessage() {
        String sender = "Sender1";
        Map<String, String> content = new HashMap<>();
        content.put("command", "/repeat");
        content.put("-n", "10");
        content.put("-m", "Message1");

        return new CommandMessage(sender, content);
    }

    private ByteBuffer serializeMessage(Message message) {
        byte[] serializedMessage = MessageSerializer.serializeMessage(message);

        ByteBuffer output = ByteBuffer.allocate(Integer.BYTES + serializedMessage.length);
        output.putInt(serializedMessage.length);
        output.put(serializedMessage);
        output.flip();

        return output;
    }
}
