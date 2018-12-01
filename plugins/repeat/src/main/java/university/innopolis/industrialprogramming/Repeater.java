package university.innopolis.industrialprogramming;

import university.innopolis.industrialprogramming.commands.CommandExecutor;
import university.innopolis.industrialprogramming.messages.Message;
import university.innopolis.industrialprogramming.messages.TextMessage;

import java.util.Map;

public class Repeater implements CommandExecutor {
    private final Map<String, String> args;

    public Repeater(Map<String, String> args) {
        this.args = args;
    }

    @Override
    public Message execute() {
        String result;
        Message msg = null;

        int n = 0;
        String m = "";
        if (args.containsKey("-n") && args.containsKey("-m")) {
            try {
                n = Integer.parseInt(args.get("-n"));
                m = args.get("-m");
            } catch (NumberFormatException e) {
                msg = getUsageMessage();
            }
        } else {
            msg = getUsageMessage();
        }

        if (msg == null) {
            result = repeatMessage(n, m);
            msg = new TextMessage(null, result);
        }

        return msg;
    }

    private String repeatMessage(int n, String m) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < n; i++) {
            str.append(m).append("\n");
        }
        return str.toString();
    }

    private Message getUsageMessage() {
        String str = "/repeat [-n <number of times>] [-m <message>]";
        return new TextMessage(null, str);
    }
}
