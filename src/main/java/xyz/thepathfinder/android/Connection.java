package xyz.thepathfinder.android;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Controls access the web socket connection with the Pathfinder sever.
 * To gain access to the connection use {@link PathfinderServices#getConnection()}.
 *
 * @author David Robinson
 */
class Connection extends Endpoint {

    /**
     * Logs actions performed by the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(Action.class);

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
     * Stores messages while the connection is down.
     */
    private Queue<String> messageQueue;

    /**
     * Constructs a connection object that controls access to the web socket connection
     * with the Pathfinder Server.
     *
     * @param userCredentials the user's credentials for this application
     */
    protected Connection(String userCredentials) {
        this.userCredentials = userCredentials;
        this.sentMessageCount = 0L;
        this.messageQueue = new LinkedList<String>();
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
     * Sends a message through the web socket connection to the Pathfinder server.
     *
     * @param message to be send.
     */
    private void send(String message) {
        logger.info("Sending json to Pathfinder: " + message);
        this.session.getAsyncRemote().sendText(message);
        this.sentMessageCount++;
    }

    /**
     * Sends a text message through the web socket to the Pathfinder server if connected.
     * It will save the message in a queue if not connected.
     *
     * @param message to be sent.
     */
    public void sendMessage(String message) {
        if (this.isConnected()) {
            this.send(message);
        } else {
            logger.warn("Attempting to send message while websocket is not open. Storing message until connection opens: " + message);
            this.messageQueue.add(message);
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

        logger.info("Sending stored messages");
        for (String message : this.messageQueue) {
            this.send(message);
        }
        logger.info("End sending stored messages");
        this.messageQueue.clear();
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
