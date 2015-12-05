package xyz.thepathfinder.android;


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
        //JSONObject json = new JSONObject(message);
    }

    public int getReceivedMessageCount() {
        return this.receivedMessageCount;
    }
}
