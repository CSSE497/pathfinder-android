package xyz.thepathfinder.android;


import javax.websocket.MessageHandler;

public class PathfinderMessageHandler implements MessageHandler.Whole<String> {
    public PathfinderMessageHandler() {

    }

    @Override
    public void onMessage(String message) {
        System.out.println(message);
        //JSONObject json = new JSONObject(message);
    }
}
