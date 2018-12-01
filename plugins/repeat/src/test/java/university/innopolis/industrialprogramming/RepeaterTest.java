package university.innopolis.industrialprogramming;

import org.junit.Test;
import university.innopolis.industrialprogramming.commands.CommandExecutor;
import university.innopolis.industrialprogramming.messages.Message;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RepeaterTest {
    @Test
    public void messageRepeated() {
        Map<String, String> args = new HashMap<>();
        args.put("-n", "2");
        args.put("-m", "\"Message1\"");

        CommandExecutor executor = new Repeater(args);
        Message repeatedMessage = executor.execute();

        String expectedRepeatedMessage = args.get("-m") + "\n" + args.get("-m") + "\n";

        assertNull(repeatedMessage.getSender());
        assertEquals(repeatedMessage.getContent(), expectedRepeatedMessage);
    }
}
