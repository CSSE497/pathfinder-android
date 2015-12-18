package xyz.thepathfinder.android;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.logging.Level;
import java.util.logging.Logger;

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
        //TODO find the things that need to be notified
        logger.log(Level.INFO, "Received json: " + message);
        this.receivedMessageCount++;
        JsonObject json = new JsonParser().parse(message).getAsJsonObject();
    }

    public int getReceivedMessageCount() {
        return this.receivedMessageCount;
    }
}
