package xyz.thepathfinder.android;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

public class Pathfinder {

    protected static String CLUSTER = "Cluster";
    protected static String COMMODITY = "Commodity";
    protected static String TRANSPORT = "Transport";

    public Pathfinder(String applicationIdentifier, String userCredentials, URI websocketUrl) throws IOException, DeploymentException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        PathfinderConnection connection = PathfinderConnection.getConnection();
        connection.setApplicationIdentifier(applicationIdentifier);
        connection.setUserCredentials(userCredentials);
        container.connectToServer(connection, websocketUrl); // blocks until connection is established, JSR 356
    }

    public Cluster getDefaultCluster() {
        return Cluster.getInstance(Path.DEFAULT_PATH);
    }

    public Cluster getCluster(String path) {
        return Cluster.getInstance(path);
    }

    public boolean isConnected() {
        return PathfinderConnection.getConnection().isConnected();
    }

    public long getSentMessageCount() {
        return PathfinderConnection.getConnection().getSentMessageCount();
    }

    public long getReceivedMessageCount() {
        return PathfinderConnection.getConnection().getReceivedMessageCount();
    }

    public void close() throws IOException {
        PathfinderConnection.getConnection().close();
    }
}
