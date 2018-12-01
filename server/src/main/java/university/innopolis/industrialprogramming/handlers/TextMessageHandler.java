package university.innopolis.industrialprogramming.handlers;

import university.innopolis.industrialprogramming.Client;
import university.innopolis.industrialprogramming.messages.Message;

public class TextMessageHandler implements MessageHandler {
    @Override
    public void handleMessage(Client client, Message message) {
        if (client.getName() == null) {
            String name = message.getSender();
            client.setName(name.trim());

            System.out.println(client.getName() + " has connected");
        } else {
            message.setSender(client.getName());
            client.appendOutgoingMessage(message);
        }
    }
}
