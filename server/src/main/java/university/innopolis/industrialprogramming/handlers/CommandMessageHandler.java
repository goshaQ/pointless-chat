package university.innopolis.industrialprogramming.handlers;

import university.innopolis.industrialprogramming.Client;
import university.innopolis.industrialprogramming.handlers.executors.CommandRunner;
import university.innopolis.industrialprogramming.loaders.CustomLoader;
import university.innopolis.industrialprogramming.messages.Message;
import university.innopolis.industrialprogramming.messages.CommandMessage;
import university.innopolis.industrialprogramming.messages.TextMessage;

import java.util.Map;

public class CommandMessageHandler implements MessageHandler {
    private final ClassLoader classLoader;

    CommandMessageHandler(String path) {
        classLoader = new CustomLoader(path);
    }

    @Override
    public void handleMessage(Client client, Message message) {
        Map<String, String> args = ((CommandMessage) message).getContent();

        String commandName;
        switch (args.get("command")) {
            case "/repeat":
                commandName = "Repeater";
                break;
            case "/factorial":
                commandName = "FactorialCalculator";
                break;
            case "/power":
                commandName = "PowerCalculator";
                break;
            default:
                Message unknownMessage = new TextMessage(null, "Unknown command!");
                client.appendIncomingMessage(unknownMessage);
                return;
        }

        Thread thread = new Thread(new CommandRunner(client, commandName, args));
        thread.setContextClassLoader(classLoader);
        thread.start();
    }
}
