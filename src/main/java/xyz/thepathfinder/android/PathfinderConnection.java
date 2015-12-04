package xyz.thepathfinder.android;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.util.LinkedList;
import java.util.Queue;

public class PathfinderConnection extends Endpoint {

    private String applictionIdentifier;
    private String userCredentials;
    private Session session;
    private EndpointConfig endpointConfig;
    private Queue<String> outgoingMessageQueue;

    public PathfinderConnection(String applicationIdentifier, String userCredentials) {
        this.applictionIdentifier = applicationIdentifier;
        this.userCredentials = userCredentials;
        outgoingMessageQueue = new LinkedList<String>();
    }

    public void sendMessage(String message) {
        System.out.println("Sending json: " + message);
        if(this.isConnected()) {
            this.session.getAsyncRemote().sendText(message);
        } else if(outgoingMessageQueue != null) {
            this.outgoingMessageQueue.add(message);
        } else {
            throw new IllegalStateException("The connection to Pathfinder was closed or opened twice.");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        this.session.addMessageHandler(new PathfinderMessageHandler());
        this.endpointConfig = config;

        for(String message: this.outgoingMessageQueue) {
            this.sendMessage(message);
        }
        this.outgoingMessageQueue = null;
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        this.session = session;
    }

    public boolean isConnected() {
        return this.session.isOpen();
    }
}
