package university.innopolis.industrialprogramming.handlers.executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import university.innopolis.industrialprogramming.Client;
import university.innopolis.industrialprogramming.commands.CommandExecutor;
import university.innopolis.industrialprogramming.messages.Message;

import java.lang.reflect.Proxy;
import java.util.Map;

public class CommandRunner implements Runnable {
    private static Logger logger = LogManager.getLogger(CommandRunner.class);

    private final Client client;
    private final String commandName;
    private final Map<String, String> args;


    public CommandRunner(Client client, String className, Map<String, String> args) {
        this.client = client;
        this.commandName = className;
        this.args = args;
    }

    @Override
    public void run() {
        CommandExecutor commandExecutor;

        try {
            commandExecutor = (CommandExecutor) Proxy.newProxyInstance(
                    CommandExecutor.class.getClassLoader(),
                    new Class[]{CommandExecutor.class},
                    new CommandExecutorInvocationHandler(commandName, args));
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
            return;
        }

        Message message = commandExecutor.execute();
        if (message != null) {
            client.appendIncomingMessage(message);
        }
    }
}
