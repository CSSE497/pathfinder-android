package xyz.thepathfinder.android;

import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * The <tt>Pathfinder</tt> class is the main entry point to the Pathfinder API. To create the Pathfinder class you need
 * a valid application identifier provided from your Pathfinder service manager, we provide the ability to get
 * an application identifier from <a href="http://thepathfinder.xyz">thepathfinder.xyz</a>. The connection also
 * requires a JWT in the form of a String to authenticate the user. The URI to your pathfinder provider is also
 * required to initiate the connection.
 * </p>
 *
 * <p>
 * The default cluster is available via the {@link #getDefaultCluster} method. Any other cluster available to
 * the user may be obtained through the {@link #getCluster(String)} method, where the path is of the form
 * <tt>"/default/clusterName/subclusterName/subsubclusterName"</tt>.
 * </p>
 *
 * <p>
 * Note, when connecting the<code>Pathfinder</code> object the thread is blocked until the web socket to the
 * Pathfinder service is opened.
 * </p>
 *
 * <pre><code>   Pathfinder pathfinder = new Pathfinder("myAppId", "UserJWT");
 *   pathfinder.connect();
 *   Cluster cluster = pathfinder.getCluster("/default/cluster1/subcluster2");
 *   MyClusterListener clusterListener = new MyClusterListener();
 *   cluster.addListener(clusterListener);
 *   cluster.connect();
 * {@literal   // more code ...}
 *   pathfinder.close();</code></pre>
 *
 * @author David Robinson
 * @version 0.0.1
 * @see Cluster
 * @see Commodity
 * @see Transport
 */
public class Pathfinder {

    /**
     * Cluster model name used in requests to the Pathfinder server.
     */
    protected static final String CLUSTER = "Cluster";

    /**
     * Commodity model name used in requests to the Pathfinder server.
     */
    protected static final String COMMODITY = "Commodity";

    /**
     * Transport model name used in requests to the Pathfinder server.
     */
    protected static final String TRANSPORT = "Vehicle";

    //TODO revert after transport update on pathfinder-server
    //protected static final String TRANSPORT = "Transport";

    /**
     * Keeps track of all the Pathfinder models and connection to the server.
     */
    private PathfinderServices services;

    /**
     * URL to the Pathfinder server being connected to.
     */
    private URI webSocketUrl;

    /**
     * Application identifier used to communicate with the pathfinder server.
     */
    private String applicationIdentifier;

    /**
     * Logs all messages
     */
    private static Logger logger = Logger.getLogger(Pathfinder.class.getName());
    static {
        logger.setLevel(Level.INFO);
    }

    /**
     * Constructs a Pathfinder object.
     *
     * @param applicationIdentifier application Identifier provided by a Pathfinder service provider
     * @param userCredentials       JWT of the user's credentials
     */
    public Pathfinder(String applicationIdentifier, String userCredentials) {
        try {
            this.webSocketUrl = new URI("ws://api.thepathfinder.xyz/socket");
        } catch(URISyntaxException e) {
            logger.severe(e.getMessage());
        }

        this.constructPathfinder(applicationIdentifier, userCredentials);
    }

    /**
     * Constructs a Pathfinder object.
     *
     * @param applicationIdentifier application Identifier provided by a Pathfinder service provider
     * @param userCredentials       JWT of the user's credentials
     * @param webSocketUrl          URL to the Pathfinder web socket service provider
     */
    protected Pathfinder(String applicationIdentifier, String userCredentials, URI webSocketUrl) {
        this.webSocketUrl = webSocketUrl;
        this.constructPathfinder(applicationIdentifier, userCredentials);
    }

    private void constructPathfinder(String applicationIdentifier, String userCredentials) {
        this.applicationIdentifier = applicationIdentifier;

        ModelRegistry registry = new ModelRegistry();
        Connection connection = new Connection(applicationIdentifier, userCredentials, registry);

        this.services = new PathfinderServices(registry, connection);
    }

    /**
     * Establishes a connection to the Pathfinder server, if the connection is not already open.
     * This method blocks until the connection is established.
     *
     * @throws IOException problem connecting the Pathfinder server
     */
    public void connect() throws IOException, DeploymentException {
        if (!this.isConnected()) {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            try {
                // blocks until connection is established, JSR 356
                container.connectToServer(this.services.getConnection(), this.webSocketUrl);
            } catch (DeploymentException e) {
                logger.severe("Deployment Exception: " + e.getMessage()); // Invalid annotated connection object
                // throw e;
            } catch (IOException e) {
                logger.severe("IO Exception: " + e.getMessage());
                throw e;
            }
        }
    }

    /**
     * Gets an unconnected cluster pointing to the default cluster for the application identifier provided.
     *
     * @return an unconnected cluster
     */
    public Cluster getDefaultCluster() {
        return Cluster.getInstance(this.applicationIdentifier, this.services);
    }

    /**
     * Gets an unconnected cluster pointing to the path specified.
     *
     * @param path to the cluster
     * @return an unconnected cluster
     */
    public Cluster getCluster(String path) {
        return Cluster.getInstance(path, this.services);
    }

    /**
     * Gets an unconnected commodity pointing to the path specified.
     *
     * @param path to the commodity
     * @return an unconnected commodity
     */
    public Commodity getCommodity(String path) {
        return Commodity.getInstance(path, this.services);
    }

    /**
     * Gets an unconnected transport pointing to the path specified.
     *
     * @param path to the transport
     * @return an unconnected transport
     */
    public Transport getTransport(String path) {
        return Transport.getInstance(path, this.services);
    }

    /**
     * Returns <tt>true</tt> if the web socket connection to the Pathfinder server is open.
     *
     * @return <tt>true</tt> if the connection is still open
     */
    public boolean isConnected() {
        return this.services.getConnection().isConnected();
    }

    /**
     * Returns the number of web socket messages sent to the Pathfinder server.
     *
     * @return The number of web socket messages sent
     */
    protected long getSentMessageCount() {
        return this.services.getConnection().getSentMessageCount();
    }

    /**
     * Returns the number of web socket messages received from the Pathfinder server.
     *
     * @return The number of web socket messsages received.
     */
    protected long getReceivedMessageCount() {
        return this.services.getConnection().getReceivedMessageCount();
    }

    /**
     * Closes the web socket connection to the Pathfinder server with a normal close condition.
     *
     * @throws IOException If there was error closing the connection.
     */
    public void close() throws IOException {
        CloseReason reason = new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Connection ended by user");
        this.close(reason);
    }

    /**
     * Closes the web socket connection to the Pathfinder server, if it is still open, with the specified reason.
     *
     * @param reason The reason to close the connection.
     * @throws IOException If there was error closing the connection.
     */
    public void close(CloseReason reason) throws IOException {
        if (this.isConnected()) {
            logger.info("Connection closed");
            this.services.getConnection().close(reason);
        }
    }
}
