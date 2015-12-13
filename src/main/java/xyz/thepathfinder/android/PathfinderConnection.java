package xyz.thepathfinder.android;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;

public class PathfinderConnection extends Endpoint {

    private String applictionIdentifier;
    private String userCredentials;
    private Session session;
    private long sentMessageCount;
    private PathfinderMessageHandler messageHandler;

    protected PathfinderConnection(String applicationIdentifier, String userCredentials) {
        this.applictionIdentifier = applicationIdentifier;
        this.userCredentials = userCredentials;
        this.sentMessageCount = 0L;
    }

    private PathfinderMessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    public void addMessageReceiver(SubscribableModel subscribableModel) {
        this.getMessageHandler().addMessageReceiver(subscribableModel);
    }

    public SubscribableModel removeMessageReceiver(SubscribableModel subscribableModel) {
        return this.getMessageHandler().removeMessageReceiver(subscribableModel);
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
        this.session = session;
        this.messageHandler = new PathfinderMessageHandler();
        this.session.addMessageHandler(this.messageHandler);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        this.session = session;
    }

    public boolean isConnected() {
        return this.session.isOpen();
    }

    protected String getApplictionIdentifier() {
        return this.applictionIdentifier;
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
