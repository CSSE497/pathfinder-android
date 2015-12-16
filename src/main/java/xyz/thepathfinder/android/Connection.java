package xyz.thepathfinder.android;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection extends Endpoint {

    private final String applictionIdentifier;
    private final String userCredentials;
    private final ModelRegistry registry;
    private Session session;
    private long sentMessageCount;
    private MessageHandler messageHandler;
    private Logger logger = Logger.getLogger(Connection.class.getName());

    protected Connection(String applictionIdentifier, String userCredentials, ModelRegistry registry) {
        this.applictionIdentifier = applictionIdentifier;
        this.userCredentials = userCredentials;
        this.registry = registry;
        this.sentMessageCount = 0L;
    }

    public void sendMessage(String message) {
        logger.log(Level.INFO, "Sending json: " + message);
        if(this.isConnected()) {
            this.session.getAsyncRemote().sendText(message);
            this.sentMessageCount++;
        } else {
            throw new IllegalStateException("The connection to Pathfinder was closed or opened twice.");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        logger.log(Level.INFO, "Connection opened");
        this.session = session;
        this.messageHandler = new MessageHandler(this.registry);
        this.session.addMessageHandler(this.messageHandler);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        logger.log(Level.INFO, "Connection closed: " + closeReason);
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
