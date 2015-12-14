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
    private PathfinderServices services;

    public Pathfinder(String applicationIdentifier, String userCredentials, URI websocketUrl) throws IOException, DeploymentException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        PathfinderConnection connection = new PathfinderConnection(applicationIdentifier, userCredentials);
        PathfinderModelRegistry registry = new PathfinderModelRegistry();

        this.services = new PathfinderServices(registry, connection);

        container.connectToServer(connection, websocketUrl); // blocks until connection is established, JSR 356
    }

    public Cluster getDefaultCluster() {
        return Cluster.getInstance(Path.DEFAULT_PATH, this.services);
    }

    public Cluster getCluster(String path) {
        return Cluster.getInstance(path, this.services);
    }

    public boolean isConnected() {
        return this.services.getConnection().isConnected();
    }

    public long getSentMessageCount() {
        return this.services.getConnection().getSentMessageCount();
    }

    public long getReceivedMessageCount() {
        return this.services.getConnection().getReceivedMessageCount();
    }

    public void close() throws IOException {
        this.services.getConnection().close();
    }
}
