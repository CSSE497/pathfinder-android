package xyz.pathfinder.android;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public class PathfinderConnection extends Endpoint {

    private String applictionIdentifier;
    private String userCredentials;
    private Session session;
    private EndpointConfig endpointConfig;
    private boolean connected;

    public PathfinderConnection(String applicationIdentifier, String userCredentials) {
        this.applictionIdentifier = applicationIdentifier;
        this.userCredentials = userCredentials;
        this.connected = false;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        this.endpointConfig = config;
        this.connected = true;
    }

    public boolean isConnected() {
        return this.connected;
    }
}
