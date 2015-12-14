package xyz.thepathfinder.android;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;

public class PathfinderConnection extends Endpoint {

    private static PathfinderConnection connection = new PathfinderConnection();

    private String applictionIdentifier;
    private String userCredentials;
    private Session session;
    private long sentMessageCount;
    private PathfinderMessageHandler messageHandler;

    private PathfinderConnection() {
        this.sentMessageCount = 0L;
    }

    protected static PathfinderConnection getConnection() {
        if(PathfinderConnection.connection == null) {
            PathfinderConnection.connection = new PathfinderConnection();
        }

        return PathfinderConnection.connection;
    }

    protected void setApplicationIdentifier(String applicationIdentifier) {
        this.applictionIdentifier = applicationIdentifier;
    }

    protected void setUserCredentials(String userCredentials) {
        this.userCredentials = userCredentials;
    }

    public void sendMessage(String message) {
        System.out.println("Sending json: " + message);
        if(this.isConnected()) {
            PathfinderConnection.getConnection().session.getAsyncRemote().sendText(message);
            this.sentMessageCount++;
        } else {
            throw new IllegalStateException("The connection to Pathfinder was closed or opened twice.");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        System.out.print("Connected");
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
