package xyz.thepathfinder.android;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author David Robinson
 */
public class MessageHandler implements javax.websocket.MessageHandler.Whole<String> {

    private final ModelRegistry registry;
    private Logger logger = Logger.getLogger(MessageHandler.class.getName());

    private int receivedMessageCount;

    protected MessageHandler(ModelRegistry registry) {
        this.registry = registry;
        this.receivedMessageCount = 0;
    }

    @Override
    public void onMessage(String message) {
        logger.log(Level.INFO, "Received json: " + message);
        this.receivedMessageCount++;
        JsonObject json = new JsonParser().parse(message).getAsJsonObject();
        if (!json.has("message") || !json.has("model") || !json.has("path")) {
            logger.log(Level.WARNING, "Ignoring invalid message: " + json.toString());
        } else {
            String type = json.get("message").getAsString();
            String path = json.get("path").getAsString();
            Model model = this.registry.getModel(path);

            logger.log(Level.INFO, "Notifying " + model.getPath() + " of message");

            model.notifyUpdate(type, json);
        }
    }

    public int getReceivedMessageCount() {
        return this.receivedMessageCount;
    }
}
