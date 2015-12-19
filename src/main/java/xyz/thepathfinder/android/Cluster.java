package xyz.thepathfinder.android;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * A cluster represents a set of commodities, sub-clusters, and transports.
 * A cluster is used to group related models. That relation could be geographic
 * type of transportation, or anything else. To sync the cluster with a
 * cluster on the Pathfinder server use the {@link Cluster#connect} method.
 * </p>
 *
 * <p>
 * Note, that clusters, as are all models, are implemented as singletons.
 * If a cluster already exists with the same path it will be returned, not a
 * new object. Use {@link Cluster#getInstance} to retrieve an cluster object.
 * </p>
 *
 * @see Commodity
 * @see ClusterListener
 * @see Transport
 */
public class Cluster extends SubscribableCrudModel<ClusterListener> {

    /**
     * String used in the model field of the pathfinder requests.
     */
    private static final String MODEL = Pathfinder.CLUSTER;

    /**
     * Maps a path in the form of a string to the transports directly under this cluster
     */
    private Map<String, Transport> transports;

    /**
     * Maps a path in the form of a string to the commodities directly under this cluster
     */
    private Map<String, Commodity> commodities;

    /**
     * Maps a path in the form of a string to the subclusters directly under this cluster
     */
    private Map<String, Cluster> subclusters;

    /**
     * List of routes for this cluster. The routes object is an immutable list.
     * The object should always be reassigned, never mutated.
     */
    private List<Route> routes;

    /**
     * Constructor for a cluster object. This should called by {@link #getInstance(String, PathfinderServices)}.
     * Each path must only refer to one object.
     * @param path The path name of the cluster.
     * @param services A service object to send messages to the server and keep track of all
     *                 {@link Model} objects.
     */
    private Cluster(String path, PathfinderServices services) {
        super(path, services);

        this.transports = new HashMap<String, Transport>();
        this.commodities = new HashMap<String, Commodity>();
        this.subclusters = new HashMap<String, Cluster>();
        this.routes = new ArrayList<Route>();

        boolean isRegistered = this.getServices().getRegistry().isModelRegistered(path);
        if(isRegistered) {
            throw new IllegalArgumentException("Cluster path already exists: " + path);
        } else {
            this.getServices().getRegistry().registerModel(this);
        }
    }

    /**
     * Returns an instance of cluster object based on the path parameter. If there is
     * a cluster with that path already created, it will return that cluster object.
     * If there isn't a cluster with that path already created, it will create a new
     * cluster object. If the path is associated with a different model type it will
     * throw a <tt>ClassCastException</tt>.
     * @param path The full path to pathfinder model
     * @param services The pathfinder services object.
     * @return A cluster with the specified path.
     * @throws ClassCastException if the requested path is already associated with a
     *         different {@link Model} type.
     */
    protected static Cluster getInstance(String path, PathfinderServices services) {
        Cluster cluster = (Cluster) services.getRegistry().getModel(path, Cluster.MODEL);

        if(cluster == null) {
            cluster = new Cluster(path, services);
        }

        return cluster;
    }

    /**
     * Returns an instance of cluster object based on the path in <tt>clusterJson</tt>. If there is
     * a cluster with that path already created, it will return that cluster object with updated fields.
     * If there isn't a cluster with that path already created, it will create a new
     * cluster object and update the fields. If the path is associated with a different model type or
     * the json will not parse to a cluster object's required fields it will throw a
     * <tt>ClassCastException</tt>.
     * @param clusterJson A json object that parses to a cluster.
     * @param services The pathfinder services object.
     * @return A cluster with the specified path.
     * @throws ClassCastException if the requested path is already associated with a
     *         different {@link Model} type. Also, if the json object doesn't parse to
     *         a cluster object.
     */
    protected static Cluster getInstance(JsonObject clusterJson, PathfinderServices services) {
        boolean canParseToCluster = Cluster.checkClusterFields(clusterJson);

        if(!canParseToCluster) {
            throw new ClassCastException("JSON could not be parsed to a cluster");
        }

        String path = Cluster.getPath(clusterJson);
        Cluster cluster = Cluster.getInstance(path, services);

        cluster.setClusterFields(clusterJson);
        return cluster;
    }

    /**
     * Checks the json object for all the required fields of a cluster object.
     * @param clusterJson JsonObject to check if it will parse to a cluster.
     * @return Whether the json can be parsed to a cluster.
     */
    private static boolean checkClusterFields(JsonObject clusterJson) {
        return clusterJson.has("path") &&
                clusterJson.has("transports") &&
                clusterJson.get("transports").isJsonArray() &&
                clusterJson.has("commodities") &&
                clusterJson.get("commodities").isJsonArray() &&
                clusterJson.has("subClusters") &&
                clusterJson.get("subClusters").isJsonArray();
    }

    /**
     * Returns the path as a string from a JSON object formatted as cluster.
     * @param clusterJson JSON object that represents a cluster
     * @return The path of the cluster
     */
    private static String getPath(JsonObject clusterJson) {
        return clusterJson.get("path").getAsString();
    }

    /**
     * Returns a list of commodities from a JSON object representing a cluster.
     * @param clusterJson JSON object that represents a cluster
     * @param services a pathfinder services object
     * @return A list of commodities from the JSON object
     */
    private static List<Commodity> getCommodities(JsonObject clusterJson, PathfinderServices services) {
        List<Commodity> commodities = new LinkedList<Commodity>();
        for(JsonElement commodity: clusterJson.getAsJsonArray("commodities")) {
            commodities.add(Commodity.getInstance(commodity.getAsJsonObject(), services));
        }
        return commodities;
    }

    /**
     * Returns a list of sub-clusters from a JSON object representing a cluster.
     * @param clusterJson JSON object that represents a cluster
     * @param services a pathfinder services object
     * @return A list of sub-clusters from the JSON object
     */
    private static List<Cluster> getSubclusters(JsonObject clusterJson, PathfinderServices services) {
        List<Cluster> clusters = new LinkedList<Cluster>();
        for(JsonElement cluster: clusterJson.getAsJsonArray("subClusters")) {
            clusters.add(Cluster.getInstance(cluster.getAsJsonObject(), services));
        }
        return clusters;
    }

    /**
     * Returns a list of transports from a JSON object representing a cluster.
     * @param clusterJson JSON object that represents a cluster
     * @param services a pathfinder services object
     * @return A list of transports from the JSON object
     */
    private static List<Transport> getTransports(JsonObject clusterJson, PathfinderServices services) {
        List<Transport> transports = new LinkedList<Transport>();
        for(JsonElement transport: clusterJson.getAsJsonArray("transports")) {
            transports.add(Transport.getInstance(transport.getAsJsonObject(), services));
        }
        return transports;
    }

    /**
     * Sets this cluster's fields to the values specified in the JSON object representing a cluster.
     * @param clusterJson JSON object that represents a cluster
     */
    private void setClusterFields(JsonObject clusterJson) {
        List<Commodity> commodities = Cluster.getCommodities(clusterJson, this.getServices());
        this.setCommodities(commodities);

        List<Transport> transports = Cluster.getTransports(clusterJson, this.getServices());
        this.setTransports(transports);

        List<Cluster> subclusters = Cluster.getSubclusters(clusterJson, this.getServices());
        this.setSubclusters(subclusters);
    }

    /**
     * Creates an unconnected commodity under this cluster.
     * @param name Name of the commodity, it must form a unique identifier when concatenated with this cluster's path.
     * @param startLatitude The pick up latitude of the commodity.
     * @param startLongitude The pick up longitude of the commodity.
     * @param endLatitude The drop off latitude of the commodity.
     * @param endLongitude The drop off longitude of the commodity.
     * @param status The current status of the commodity. If <tt>null</tt> it will default to <tt>CommodityStatus.OFFLINE</tt>.
     * @param metadata A JsonObject representing the metadata field of the commodity. If <tt>null</tt> it will default to an empty JsonObject.
     * @return An unconnected commodity.
     * @throws IllegalStateException when this cluster is not connected.
     */
    public Commodity createCommodity(String name, double startLatitude, double startLongitude, double endLatitude, double endLongitude, CommodityStatus status, JsonObject metadata) {
        if (!this.isConnected()) {
            throw new IllegalStateException("Not connected to cluster, cannot create commodity");
        }

        String path = this.getChildPath(name);
        return new Commodity(path, startLatitude, startLongitude, endLatitude, endLongitude, status, metadata, this.getServices());
    }

    /**
     * Creates a commodity with a values specified in the JSON object.
     * @param commodityJson a JSON object that represents a commodity
     * @param services a pathfinder services object
     * @return A commodity with the values in the JSON object
     */
    private Commodity createCommodity(JsonObject commodityJson, PathfinderServices services) {
        return Commodity.getInstance(commodityJson, services);
    }

    /**
     * Returns a commodity directly under this cluster by its name.
     * @param name The name of the commodity.
     * @return A commodity associated with that name if one exists, <tt>null</tt> if it doesn't exist.
     */
    public Commodity getCommodity(String name) {
        return this.commodities.get(this.getChildPath(name));
    }

    /**
     * Returns an immutable collection of this cluster's commodities.
     * @return A collection of commodities.
     */
    public Collection<Commodity> getCommodities() {
        return Collections.unmodifiableCollection(this.commodities.values());
    }

    /**
     * Sets this cluster's commodities.
     * @param commodities an iterable collection of commodities
     */
    private void setCommodities(Iterable<Commodity> commodities) {
        for(Commodity commodity: commodities) {
            this.commodities.put(commodity.getPath(), commodity);
        }
    }

    /**
     * Creates an unconnected subcluster under this cluster.
     * @param name Name of the subcluster, it must form a unique identifier when concatenated with this cluster's path.
     * @return An unconnected subcluster.
     * @throws IllegalStateException when this cluster is not connected.
     */
    public Cluster createSubcluster(String name) {
        if(!this.isConnected()) {
            throw new IllegalStateException("The cluster is not connected on the Pathfinder server");
        }

        return Cluster.getInstance(this.getChildPath(name), this.getServices());
    }

    /**
     * Returns a direct descendant subcluster by its name.
     * @param name The name of the subcluster
     * @return A subcluster associated with the provided name if exists, <tt>null</tt> if it doesn't exist.
     */
    public Cluster getSubcluster(String name) {
        return this.subclusters.get(this.getChildPath(name));
    }

    /**
     * Returns an immutable collection of this cluster's direct subclusters.
     * @return An immutable collection of clusters.
     */
    public Collection<Cluster> getSubclusters() {
        return Collections.unmodifiableCollection(this.subclusters.values());
    }

    /**
     * Sets this cluster's sub-clusters.
     * @param subclusters an iterable collection of clusters
     */
    private void setSubclusters(Iterable<Cluster> subclusters) {
        for(Cluster cluster: subclusters) {
            this.subclusters.put(cluster.getPath(), cluster);
        }
    }

    /**
     * Creates an unconnected transport under this cluster.
     * @param name Name of the transport, it must form a unique identifier when concatenated with this cluster's path.
     * @param latitude The current latitude of the transport.
     * @param longitude The current longitude of the transport.
     * @param status The current status of the transport. If <tt>null</tt> it defaults to <tt>TransportStatus.OFFLINE</tt>
     * @param metadata The transports's metadata field. If <tt>null</tt> it defaults to an empty JSON object.
     * @return An unconnected transport
     * @throws IllegalStateException when this cluster has not been connected.
     */
    public Transport createTransport(String name, double latitude, double longitude, TransportStatus status, JsonObject metadata) {
        if (!this.isConnected()) {
            throw new IllegalStateException("Not connected to cluster, cannot create transport");
        }

        String path = this.getChildPath(name);
        return new Transport(path, latitude, longitude, status, metadata, this.getServices());
    }

    /**
     * Creates a connected transport under this cluster with the values in the JSON object provided.
     * @param transportJson a JSON object that represents a transport.
     * @return A transport with the values in the JSON object.
     */
    private Transport createTransport(JsonObject transportJson) {
        return Transport.getInstance(transportJson, this.getServices());
    }

    /**
     * Returns a transport directly under this cluster by its name.
     * @param name The name of the transport.
     * @return A transport associated with the provided name if exists, <tt>null</tt> if it doesn't exist.
     */
    public Transport getTransport(String name) {
        return this.transports.get(this.getChildPath(name));
    }

    /**
     * Returns an immutable collection of this cluster's transports.
     * @return A collection of transports.
     */
    public Collection<Transport> getTransports() {
        return Collections.unmodifiableCollection(this.transports.values());
    }

    /**
     * Sets this cluster's transports.
     * @param transports an iterable collection of transports
     */
    private void setTransports(Iterable<Transport> transports) {
        for(Transport transport: transports) {
            this.transports.put(transport.getPath(), transport);
        }
    }

    /**
     * Sets this cluster's routes.
     * @param routes a list of routes for this cluster
     */
    private void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    /**
     * Returns an immutable collection of this cluster's routes.
     * @return A collection of routes.
     */
    public List<Route> getRoutes() {
        return this.routes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getModel() {
        return Cluster.MODEL;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected JsonObject createValueJson() {
        JsonObject json = new JsonObject();

        json.addProperty("path", this.getPath());
        json.addProperty("model", this.getModel());

        return json;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void notifyUpdate(JsonObject json) {
        // IMPLEMENTME: 12/12/15
    }
}
