package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;

public class Pathfinder {

    private PathfinderConnection connection;

    public Pathfinder(String applicationIdentifier, String userCredentials, URI websocketUrl) throws IOException, DeploymentException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.connection = new PathfinderConnection(applicationIdentifier, userCredentials);
        container.connectToServer(this.connection, websocketUrl);

        JsonObject jsonId = new JsonObject();
        jsonId.addProperty("id", applicationIdentifier);

        JsonObject json = new JsonObject();
        json.add("getApplicationCluster", jsonId);
        this.connection.sendMessage(json.toString());
    }

    public Cluster cluster() {
        return this.cluster(Cluster.DEFAULT_PATH);
    }

    public Cluster cluster(String path) {
        return new Cluster(path, this.connection);
    }

    public boolean isConnected() {
        return this.connection.isConnected();
    }

}
