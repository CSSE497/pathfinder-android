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
    private static final Logger logger = LoggerFactory.getLogger(Connection.class);

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
     */
    protected Connection() {
        this.sentMessageCount = 0L;
        this.messageQueue = new LinkedList<>();
    }

    /**
     * Sets the web socket connection's message handler. If the message handler is not
     * of the type {@link AuthenticationMessageHandler} it will attempt to send all
     * of the backed up messages until the user was authenticated.
     *
     * @param messageHandler to receive the web socket messages.
     */
    protected void setMessageHandler(MessageHandler messageHandler) {
        if (this.session != null) {
            this.session.removeMessageHandler(this.messageHandler);
            this.session.addMessageHandler(messageHandler);
        }
        this.messageHandler = messageHandler;

        if (this.session != null && !(this.messageHandler instanceof AuthenticationMessageHandler)) {
            logger.info("Sending stored messages");
            for (String message : this.messageQueue) {
                this.send(message);
            }

            logger.info("End sending stored messages");
            this.messageQueue.clear();
        }
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
        if (this.isConnected() && !(this.messageHandler instanceof AuthenticationMessageHandler)) {
            this.send(message);
        } else {
            logger.warn("Attempting to send message while websocket is not open. Storing message until connection opens: " + message);
            this.messageQueue.add(message);
        }
    }

    /**
     * Sends an authentication messages that bypasses the message queue.
     *
     * @param message to send to the pathfinder server.
     */
    protected void sendAuthenticationMessage(String message) {
        this.send(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        logger.info("Pathfinder connection opened");
        this.session = session;
        this.session.addMessageHandler(this.messageHandler);

        if (!(this.messageHandler instanceof AuthenticationMessageHandler)) {
            logger.info("Sending stored messages");
            for (String message : this.messageQueue) {
                this.send(message);
            }

            logger.info("End sending stored messages");
            this.messageQueue.clear();
        }
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
     * {@inheritDoc}
     */
    @Override
    public void onError(Session session, Throwable throwable) {
        logger.error("Pathfinder websocket connection broke: " + throwable.getMessage());
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
    protected long getSentMessageCount() {
        return this.sentMessageCount;
    }

    /**
     * Returns the number of messages receive through the web socket.
     *
     * @return the number of messages received.
     */
    protected long getReceivedMessageCount() {
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
