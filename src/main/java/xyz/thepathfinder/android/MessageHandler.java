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
    static {
        logger.setLevel(Level.INFO);
    }

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
        try {
            logger.info("Received json: " + message);
            this.receivedMessageCount++;
            JsonObject json = new JsonParser().parse(message).getAsJsonObject();

            if (!json.has("message") || !json.has("model")) {
                logger.warning("Ignoring invalid message: " + json.toString());
            }
            //TODO revert after path update
        /*if (!json.has("message") || !json.has("model") || !json.has("path")) {
            logger.warning("Ignoring invalid message: " + json.toString());
        }*/
            else {
                String type = json.get("message").getAsString();

                //TODO revert after path update
                JsonObject value = json.getAsJsonObject("value");
                String path = "";
                if (json.has("id")) {
                    path += json.get("id").getAsString();
                } else if (json.has("clusterId")) {
                    path += json.get("clusterId").getAsString();
                } else if (!json.get("model").getAsString().equals(Pathfinder.CLUSTER)) {
                    path += value.get("clusterId").getAsString();
                    path += "/" + value.get("id").getAsString();
                } else {
                    path += value.get("id").getAsString();
                }
                //String path = json.get("path").getAsString();
                //End TODO

                Model model = this.registry.getModel(path);

                if(model != null) {
                    logger.info("Notifying " + model.getPath() + " of message");

                    model.notifyUpdate(type, json);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
