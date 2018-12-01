package university.innopolis.industrialprogramming.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TextMessage implements Message {
    private String sender;
    private String content;

    @JsonCreator
    public TextMessage(@JsonProperty("sender") String sender, @JsonProperty("content") String content) {
        this.sender = sender;
        this.content = content;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return sender + ": " + content;
    }
}
