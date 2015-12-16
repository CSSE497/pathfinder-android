package xyz.thepathfinder.android;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

/**
 * <p>
 * The <tt>Pathfinder</tt> class is the main entry point to the Pathfinder API. To create the Pathfinder class you need
 * a valid application identifier provided from your Pathfinder service manager, we provide the ability to get
 * an application identifier from <a href="http://thepathfinder.xyz">thepathfinder.xyz</a>. The connection also requires a JWT in the form of a
 * String to authenticate the user. The URI to your pathfinder provider is also required to initiate the
 * connection.
 * </p>
 *
 * <p>
 * The default cluster is available via the {@link #getDefaultCluster} method. Any other cluster available to
 * the user may be obtained through the {@link #getCluster(String)} method, where the path is of the form
 * <tt>"/default/clusterName/subclusterName/subsubclusterName"</tt>.
 * </p>
 *
 * <p>
 * Note, when the creating a <code>Pathfinder</code> object the thread is blocked until the websocket to the
 * Pathfinder service is opened.
 * </p>
 *
 * @author David Robinson
 * @version 0.0.1
 */
public class Pathfinder {

    /**
     * Cluster model name used in requests to the Pathfinder server.
     */
    protected static String CLUSTER = "Cluster";

    /**
     * Commodity model name used in requests to the Pathfinder server.
     */
    protected static String COMMODITY = "Commodity";

    /**
     * Transport model name used in requests to the Pathfinder server.
     */
    protected static String TRANSPORT = "Transport";

    /**
     * Keeps track of all the Pathfinder models and connection to the server.
     */
    private PathfinderServices services;

    /**
     * Establishes a connection a Pathfinder server.
     * @param applicationIdentifier Application Identifier provided by a Pathfinder service provider
     * @param userCredentials JWT of the user's credentials
     * @param websocketUrl URL to the Pathfinder websocket service provider
     * @throws IOException If there was a problem connecting the Pathfinder server
     */
    public Pathfinder(String applicationIdentifier, String userCredentials, URI websocketUrl) throws IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        ModelRegistry registry = new ModelRegistry();
        Connection connection = new Connection(applicationIdentifier, userCredentials, registry);

        this.services = new PathfinderServices(registry, connection);

        try {
            container.connectToServer(connection, websocketUrl); // blocks until connection is established, JSR 356
        } catch(DeploymentException e) {
            // Invalid annotated connection object
            e.printStackTrace();
        }
    }

    /**
     * Gets an unconnected cluster pointing to the default cluster for the application identifier provided.
     * @return A unconnected cluster
     */
    public Cluster getDefaultCluster() {
        return Cluster.getInstance(Path.DEFAULT_PATH, this.services);
    }

    /**
     * Gets an unconnected cluster pointing to the path specified.
     * @param path path to the cluster
     * @return A unconnected cluster
     */
    public Cluster getCluster(String path) {
        return Cluster.getInstance(path, this.services);
    }

    /**
     * Gets whether or not the websocket is connected to the Pathfinder server.
     * @return Whether the connection is still open
     */
    public boolean isConnected() {
        return this.services.getConnection().isConnected();
    }

    /**
     * Returns the number of websocket messages sent to the Pathfinder server.
     * @return The number of websocket messages sent
     */
    public long getSentMessageCount() {
        return this.services.getConnection().getSentMessageCount();
    }

    /**
     * Returns the number of websocket messages received from the Pathfinder server.
     * @return The number of websocket messsages received.
     */
    public long getReceivedMessageCount() {
        return this.services.getConnection().getReceivedMessageCount();
    }

    /**
     * Close the websocket connection to the Pathfinder server.
     * @throws IOException If there was a connection error closing the connection.
     */
    public void close() throws IOException {
        this.services.getConnection().close();
    }
}
