package xyz.thepathfinder.android;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;

public class Connection extends Endpoint {

    private String applictionIdentifier;
    private String userCredentials;
    private Session session;
    private long sentMessageCount;
    private MessageHandler messageHandler;

    protected Connection(String applictionIdentifier, String userCredentials) {
        this.applictionIdentifier = applictionIdentifier;
        this.userCredentials = userCredentials;
        this.sentMessageCount = 0L;
    }

    public void sendMessage(String message) {
        System.out.println("Sending json: " + message);
        if(this.isConnected()) {
            this.session.getAsyncRemote().sendText(message);
            this.sentMessageCount++;
        } else {
            throw new IllegalStateException("The connection to Pathfinder was closed or opened twice.");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        System.out.print("Connected");
        this.session = session;
        this.messageHandler = new MessageHandler();
        this.session.addMessageHandler(this.messageHandler);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        this.session = session;
    }

    public boolean isConnected() {
        return this.session.isOpen();
    }

    public long getSentMessageCount() {
        return this.sentMessageCount;
    }

    public long getReceivedMessageCount() {
        return this.messageHandler.getReceivedMessageCount();
    }

    public void close() throws IOException {
        this.session.close();
    }
}
