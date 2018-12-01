package university.innopolis.industrialprogramming.runners.notification;

import university.innopolis.industrialprogramming.ChatClient;

public class RunListener {
    private ChatClient listener;

    public void register(ChatClient listener) {
        this.listener = listener;
    }

    public void notifyThatIOExceptionHappened() {
        listener.shutdown(1);
    }

    public void notifyExit() {
        listener.shutdown(0);
    }
}
