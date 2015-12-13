package xyz.thepathfinder.android;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

public class Pathfinder {

    protected static String CLUSTER = "Cluster";
    protected static String COMMODITY = "Commodity";
    protected static String TRANSPORT = "Vehicle";

    private PathfinderConnection connection;

    public Pathfinder(String applicationIdentifier, String userCredentials, URI websocketUrl) throws IOException, DeploymentException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.connection = new PathfinderConnection(applicationIdentifier, userCredentials);
        container.connectToServer(this.connection, websocketUrl); // blocks until connection is established, JSR 356
    }

    public Cluster getCluster() {
        return Cluster.getInstance(Path.DEFAULT_PATH, this.connection);
    }

    public Cluster getCluster(String path) {
        return Cluster.getInstance(path, this.connection);
    }

    public boolean isConnected() {
        return this.connection.isConnected();
    }

    public long getSentMessageCount() {
        return this.connection.getSentMessageCount();
    }

    public long getReceivedMessageCount() {
        return this.connection.getReceivedMessageCount();
    }

    public void close() throws IOException {
        this.connection.close();
    }
}
