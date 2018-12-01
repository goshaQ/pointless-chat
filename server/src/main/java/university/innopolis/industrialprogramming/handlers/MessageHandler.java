package university.innopolis.industrialprogramming.handlers;

import university.innopolis.industrialprogramming.Client;
import university.innopolis.industrialprogramming.messages.Message;

public interface MessageHandler {
    void handleMessage(Client client, Message message);
}
