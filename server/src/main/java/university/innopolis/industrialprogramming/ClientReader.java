package university.innopolis.industrialprogramming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import university.innopolis.industrialprogramming.handlers.MessageDispatcher;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

class ClientReader {
    private static Logger logger = LogManager.getLogger(ClientReader.class);
    private final ChatServer chatServer;
    private final MessageDispatcher messageDispatcher;

    ClientReader(ChatServer chatServer, MessageDispatcher messageDispatcher) {
        this.chatServer = chatServer;
        this.messageDispatcher = messageDispatcher;
    }

    void run(Client client) {
        beforeRead(client);
        client.read(new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if (result < 1) {
                    System.out.println("Closing connection to " + client.getName());
                    closeConnection();
                } else {
                    messageDispatcher.dispatch(client, buffer, result);
                    ClientReader.this.run(client);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                logger.error(exc.getMessage(), exc);
                closeConnection();
            }

            private void closeConnection() {
                client.close();
                chatServer.removeClient(client);
            }
        });
    }

    private void beforeRead(Client client) {
        while (client.hasNextOutgoingMessage()) {
            chatServer.writeMessageToEveryoneElse(client, client.nextOutgoingMessage());
        }
    }
}
