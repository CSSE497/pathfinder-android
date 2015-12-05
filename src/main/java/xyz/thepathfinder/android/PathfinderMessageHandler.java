package xyz.thepathfinder.android;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.websocket.MessageHandler;

public class PathfinderMessageHandler implements MessageHandler.Whole<String> {

    private int receivedMessageCount;

    protected PathfinderMessageHandler() {
        this.receivedMessageCount = 0;
    }

    @Override
    public void onMessage(String message) {
        this.receivedMessageCount++;
        System.out.println(message);
        JsonObject json = new JsonParser().parse(message).getAsJsonObject();
        //TODO find the things that need to be notified
    }

    public int getReceivedMessageCount() {
        return this.receivedMessageCount;
    }
}
