package university.innopolis.industrialprogramming;

import org.junit.Test;
import university.innopolis.industrialprogramming.commands.CommandExecutor;
import university.innopolis.industrialprogramming.messages.Message;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FactorialCalculatorTest {
    @Test
    public void factorialCalculated() {
        Map<String, String> args = new HashMap<>();
        args.put("-d", "10");

        CommandExecutor executor = new FactorialCalculator(args);
        Message calculatedFactorial = executor.execute();

        String expectedCalculatedFactorial = "3628800";

        assertNull(calculatedFactorial.getSender());
        assertEquals(calculatedFactorial.getContent(), expectedCalculatedFactorial);
    }
}
