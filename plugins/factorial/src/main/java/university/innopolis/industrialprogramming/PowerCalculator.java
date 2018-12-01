package university.innopolis.industrialprogramming;

import university.innopolis.industrialprogramming.commands.CommandExecutor;
import university.innopolis.industrialprogramming.messages.Message;
import university.innopolis.industrialprogramming.messages.TextMessage;

import java.util.Map;

public class PowerCalculator implements CommandExecutor {
    private final Map<String, String> args;

    public PowerCalculator(Map<String, String> args) {
        this.args = args;
    }

    @Override
    public Message execute() {
        String result;
        Message msg = null;

        double d = 0;
        double p = 0;
        if (args.containsKey("-d") && args.containsKey("-p")) {
            try {
                d = Double.parseDouble(args.get("-d"));
                p = Double.parseDouble(args.get("-p"));
            } catch (NumberFormatException e) {
                msg = getUsageMessage();
            }
        } else {
            msg = getUsageMessage();
        }

        if (msg == null) {
            result = raiseToPower(d, p);
            msg = new TextMessage(null, result);
        }

        return msg;
    }

    private String raiseToPower(double d, double p) {
        return Double.toString(Math.pow(d, p));
    }

    private Message getUsageMessage() {
        String str = "/power [-d <digit>] [-p <power>]";
        return new TextMessage(null, str);
    }
}
