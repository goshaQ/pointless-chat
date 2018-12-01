package university.innopolis.industrialprogramming.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class CommandMessage implements Message {
    private String sender;
    private Map<String, String> content;

    @JsonCreator
    public CommandMessage(@JsonProperty("sender") String sender, @JsonProperty("content") Map<String, String> content) {
        this.sender = sender;
        this.content = content;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public Map<String, String> getContent() {
        return content;
    }

    @Override
    public void setSender(String sender) {
        this.sender = sender;
    }
}
