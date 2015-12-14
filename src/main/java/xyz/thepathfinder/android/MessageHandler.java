package xyz.thepathfinder.android;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.websocket.MessageHandler;

public class MessageHandler implements javax.websocket.MessageHandler.Whole<String> {

    private int receivedMessageCount;

    protected MessageHandler() {
        this.receivedMessageCount = 0;
    }

    private String makeKey(SubscribableModel subscribableModel) {
        return subscribableModel.getModel() + subscribableModel.getPath();
    }

    public void addMessageReceiver(SubscribableModel subscribableModel) {
        
    }

    public SubscribableModel removeMessageReceiver(SubscribableModel subscribableModel) {
        return null;
    }

    @Override
    public void onMessage(String message) {
        this.receivedMessageCount++;
        JsonObject json = new JsonParser().parse(message).getAsJsonObject();

        if(json.has("error")) {
            throw new Error("Pathfinder error message received: " + json.toString());
        }


        //TODO find the things that need to be notified
        System.out.println("Received json: " + message);
    }

    public int getReceivedMessageCount() {
        return this.receivedMessageCount;
    }
}
