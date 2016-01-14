package xyz.thepathfinder.android;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Routes all web socket messages received to the receiving models.
 *
 * @author David Robinson
 */
class MessageHandler implements javax.websocket.MessageHandler.Whole<String> {

    /**
     * Holds access to all the models.
     */
    private final ModelRegistry registry;

    /**
     * Logs all messages
     */
    private static final Logger logger = Logger.getLogger(MessageHandler.class.getName());

    /**
     * Number of messages received, helps with testing.
     */
    private int receivedMessageCount;

    /**
     * Constructs the message handler.
     *
     * @param registry to find models.
     */
    protected MessageHandler(ModelRegistry registry) {
        this.registry = registry;
        this.receivedMessageCount = 0;
    }

    /**
     * Invoked when the web socket receives a message.
     *
     * @param message the message received as a String.
     */
    @Override
    public void onMessage(String message) {
        logger.log(Level.INFO, "Received json: " + message);
        this.receivedMessageCount++;
        JsonObject json = new JsonParser().parse(message).getAsJsonObject();

        if (!json.has("message") || !json.has("model") || !json.has("path")) {
            logger.warning("Ignoring invalid message: " + json.toString());
        } else {
            String type = json.get("message").getAsString();

            String path = json.get("path").getAsString();
            Model model = this.registry.getModel(path);

            logger.finest("Notifying " + model.getPath() + " of message");

            model.notifyUpdate(type, json);
        }
    }

    /**
     * Returns the number of message received by the web socket.
     *
     * @return number of messages received by the web socket.
     */
    public int getReceivedMessageCount() {
        return this.receivedMessageCount;
    }
}
