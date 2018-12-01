package university.innopolis.industrialprogramming;

import university.innopolis.industrialprogramming.commands.CommandExecutor;
import university.innopolis.industrialprogramming.messages.Message;
import university.innopolis.industrialprogramming.messages.TextMessage;

import java.util.Map;

public class FactorialCalculator implements CommandExecutor {
    private final Map<String, String> args;

    public FactorialCalculator(Map<String, String> args) {
        this.args = args;
    }


    @Override
    public Message execute() {
        String result;
        Message msg = null;

        int d = 0;
        if (args.containsKey("-d")) {
            try {
                d = Integer.parseInt(args.get("-d"));
            } catch (NumberFormatException e) {
                msg = getUsageMessage();
            }
        } else {
            msg = getUsageMessage();
        }

        if (msg == null) {
            result = calculateFactorial(d);
            msg = new TextMessage(null, result);
        }

        return msg;
    }

    private String calculateFactorial(int d) {
        int factorial = 1;
        for (int i = 1; i <= d; i++) {
            factorial *= i;
        }
        return Integer.toString(factorial);
    }

    private Message getUsageMessage() {
        String str = "/factorial [-d <digit>]";
        return new TextMessage(null, str);
    }
}
