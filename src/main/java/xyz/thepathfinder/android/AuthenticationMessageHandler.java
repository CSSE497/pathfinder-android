package xyz.thepathfinder.android;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Routes all web socket messages received during the authentication sequence
 * to the {@link Authenticator}.
 *
 * @author David Robinson
 * @see Authenticator
 */
public class AuthenticationMessageHandler implements MessageHandler {

    /**
     * Logs actions performed by the class.
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationMessageHandler.class);

    /**
     * Authenticator that receives all of the messages.
     */
    private final Authenticator authenticator;

    /**
     * Number of messages received by this message handler.
     */
    private int receivedMessageCount;

    /**
     * Routes all web socket messages received during the authentication sequence
     * to the {@link Authenticator}.
     *
     * @param authenticator to receive the web socket messages.
     */
    public AuthenticationMessageHandler(Authenticator authenticator) {
        this.authenticator = authenticator;
        this.receivedMessageCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(String message) {
        this.receivedMessageCount++;
        JsonObject json = new JsonParser().parse(message).getAsJsonObject();

        if (!json.has("message")) {
            logger.warn("Ignoring invalid message: " + json.toString());
            return;
        }

        String type = json.get("message").getAsString();

        logger.info("Received message of type: " + type);

        this.authenticator.notifyUpdate(type, json);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getReceivedMessageCount() {
        return this.receivedMessageCount;
    }
}
