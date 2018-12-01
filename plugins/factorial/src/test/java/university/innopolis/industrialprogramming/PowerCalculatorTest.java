package university.innopolis.industrialprogramming;

import org.junit.Test;
import university.innopolis.industrialprogramming.commands.CommandExecutor;
import university.innopolis.industrialprogramming.messages.Message;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PowerCalculatorTest {
    @Test
    public void powerCalculated() {
        Map<String, String> args = new HashMap<>();
        args.put("-d", "5.123");
        args.put("-p", "7.456");

        CommandExecutor executor = new PowerCalculator(args);
        Message calculatedPower = executor.execute();

        String expectedCalculatedPower = "195080.38203253958";

        assertNull(calculatedPower.getSender());
        assertEquals(calculatedPower.getContent(), expectedCalculatedPower);
    }
}
