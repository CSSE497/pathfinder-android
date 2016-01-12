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
    private static Logger logger = Logger.getLogger(MessageHandler.class.getName());

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

        if (!json.has("message")) {
            logger.log(Level.WARNING, "Ignoring invalid message: " + json.toString());
        }
        //TODO revert after path update
        /*if (!json.has("message") || !json.has("model") || !json.has("path")) {
            logger.log(Level.WARNING, "Ignoring invalid message: " + json.toString());
        }*/ else {
            String type = json.get("message").getAsString();

            //TODO revert after path update
            Model model;
            if(type.equals("ApplicationCluster")) {
                model = this.registry.getModel(Path.DEFAULT_PATH);
                model.setPath(json.get("clusterId").getAsString());
                ((SubscribableCrudModel) model).connect();
            } else {
                String path = json.getAsJsonObject("value").get("id").getAsString();
                model = this.registry.getModel(path);
                model.notifyUpdate(type, json);
            }
            //String path = json.get("path").getAsString();
            //Model model = this.registry.getModel(path);


            logger.log(Level.INFO, "Notifying " + model.getPath() + " of message");

            //TODO revert after path update
            //model.notifyUpdate(type, json);
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
