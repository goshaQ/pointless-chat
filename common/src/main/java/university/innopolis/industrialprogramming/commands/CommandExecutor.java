package university.innopolis.industrialprogramming.commands;

import university.innopolis.industrialprogramming.messages.Message;

import java.util.Collection;
import java.util.Map;

public interface CommandExecutor {
    Message execute();
}
