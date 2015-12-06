package xyz.thepathfinder.android;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

public class Pathfinder {

    private PathfinderConnection connection;

    public Pathfinder(String applicationIdentifier, String userCredentials, URI websocketUrl) throws IOException, DeploymentException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.connection = new PathfinderConnection(applicationIdentifier, userCredentials);
        container.connectToServer(this.connection, websocketUrl); // blocks until connection is established, JSR 356
    }

    public Cluster cluster() {
        return new Cluster(this.connection);
    }

    public Cluster cluster(String path) {
        return new Cluster(path, this.connection);
    }

    public boolean isConnected() {
        return this.connection.isConnected();
    }

    public long getSentMessageCount() {
        return this.connection.getSentMessageCount();
    }

    public long getReceivedMessageCount() {
        return this.getReceivedMessageCount();
    }
}
