package xyz.thepathfinder.android;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public class PathfinderConnection extends Endpoint {

    private String applictionIdentifier;
    private String userCredentials;
    private Session session;
    private int sentMessageCount;
    private PathfinderMessageHandler messageHandler;

    protected PathfinderConnection(String applicationIdentifier, String userCredentials) {
        this.applictionIdentifier = applicationIdentifier;
        this.userCredentials = userCredentials;
        this.sentMessageCount = 0;
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
}
