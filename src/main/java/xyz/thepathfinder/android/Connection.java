package xyz.thepathfinder.android;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls access the web socket connection with the Pathfinder sever.
 * To gain access to the connection use {@link PathfinderServices#getConnection()}.
 *
 * @author David Robinson
 */
class Connection extends Endpoint {

    /**
     * The user's credentials to the Pathfinder server.
     */
    private final String userCredentials;

    /**
     * Access to the model registry to notify {@link Model}s of incoming web socket messages.
     */
    private PathfinderServices services;

    /**
     * The web socket session used to send messages through the web socket.
     */
    private Session session;

    /**
     * Number of messages sent through the web socket.
     */
    private long sentMessageCount;

    /**
     * Handles incoming web socket messages.
     */
    private MessageHandler messageHandler;

    /**
     * Logs all outgoing messages through the web socket.
     */
    private static final Logger logger = Logger.getLogger(Connection.class.getName());
    static {
        logger.setLevel(Level.INFO);
    }

    /**
     * Constructs a connection object that controls access to the web socket connection
     * with the Pathfinder Server.
     *
     * @param userCredentials      the user's credentials for this application
     */
    protected Connection(String userCredentials) {
        this.userCredentials = userCredentials;
        this.sentMessageCount = 0L;
    }

    /**
     * Sets the pathfinder services object.
     *
     * @param services a pathfinder services object
     */
    protected void setServices(PathfinderServices services) {
        this.services = services;
        this.messageHandler = new MessageHandler(this.services);
    }

    /**
     * Sends a text message through the web socket to the Pathfinder server.
     *
     * @param message the message to be sent.
     * @throws IllegalStateException the web socket is not connected.
     */
    public void sendMessage(String message) {
        logger.info("Sending json to Pathfinder: " + message);
        if (this.isConnected()) {
            this.session.getAsyncRemote().sendText(message);
            this.sentMessageCount++;
        } else {
            logger.severe("Illegal State Exception: The connection to Pathfinder is not open.");
            throw new IllegalStateException("The connection to Pathfinder is not open.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        logger.info("Pathfinder connection opened");
        this.session = session;
        this.session.addMessageHandler(this.messageHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("Pathfinder connection closed: " + closeReason);
        this.session = session;
    }

    /**
     * Returns if the web socket is connected.
     *
     * @return <tt>true</tt> if the web socket is connected, <tt>false</tt> otherwise.
     */
    public boolean isConnected() {
        return this.session != null && this.session.isOpen();
    }

    /**
     * Returns the number of messages sent through the web socket.
     *
     * @return the number of messages sent.
     */
    public long getSentMessageCount() {
        return this.sentMessageCount;
    }

    /**
     * Returns the number of messages receive through the web socket.
     *
     * @return the number of messages received.
     */
    public long getReceivedMessageCount() {
        return this.messageHandler.getReceivedMessageCount();
    }

    /**
     * Closes the web socket connection with the Pathfinder server.
     *
     * @param reason the reason for closing the web socket.
     * @throws IOException if the web socket failed to close properly.
     */
    public void close(CloseReason reason) throws IOException {
        this.session.close(reason);
    }
}
