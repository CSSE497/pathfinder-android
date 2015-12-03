package xyz.pathfinder.android;

import java.io.IOException;
import java.net.URI;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

public class Pathfinder {

    private PathfinderConnection connection;

    public Pathfinder(String applicationIdentifier, String userCredentials, URI websocketUrl) throws IOException, DeploymentException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.connection = new PathfinderConnection(applicationIdentifier, userCredentials);
        container.connectToServer(this.connection, websocketUrl);
    }

    public boolean isConnected() {
        return this.connection.isConnected();
    }

}
