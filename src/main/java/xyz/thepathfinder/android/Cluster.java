package xyz.thepathfinder.android;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
 * @author David Robinson
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
     * Maps a path in the form of a string to the commodities directly under this cluster
     */
    private Map<String, Commodity> commodities;

    /**
     * Maps a path in the form of a string to the sub-clusters directly under this cluster
     */
    private Map<String, Cluster> subclusters;

    /**
     * Maps a path in the form of a string to the transports directly under this cluster
     */
    private Map<String, Transport> transports;

    /**
     * List of routes for this cluster. The routes object is an immutable list.
     * The object should always be reassigned, never mutated.
     */
    private List<Route> routes;

    /**
     * Constructor for a cluster object. This should called by {@link #getInstance(String, PathfinderServices)}.
     * Each path must only refer to one object.
     *
     * @param path     The path name of the cluster.
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
        if (isRegistered) {
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
     * throw a <tt>IllegalArgumentException</tt>.
     *
     * @param path     The full path to pathfinder model
     * @param services The pathfinder services object.
     * @return A cluster with the specified path. If path is an empty string it returns
     * <tt>null</tt>.
     * @throws IllegalArgumentException if the requested path is already associated with a
     *                            different {@link Model} type.
     */
    protected static Cluster getInstance(String path, PathfinderServices services) {
        Cluster cluster = (Cluster) services.getRegistry().getModel(path);

        if (cluster == null && !path.equals("")) {
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
     * <tt>IllegalArgumentException</tt>.
     *
     * @param clusterJson A json object that parses to a cluster.
     * @param services    The pathfinder services object.
     * @return A cluster with the specified path.
     * @throws IllegalArgumentException if the requested path is already associated with a
     *                            different {@link Model} type. Also, if the json object doesn't parse to
     *                            a cluster object.
     */
    protected static Cluster getInstance(JsonObject clusterJson, PathfinderServices services) {
        boolean canParseToCluster = Cluster.checkClusterFields(clusterJson);

        if (!canParseToCluster) {
            throw new IllegalArgumentException("JSON could not be parsed to a cluster");
        }

        String path = Cluster.getPath(clusterJson);
        Cluster cluster = Cluster.getInstance(path, services);

        cluster.notifyUpdate(null, clusterJson);

        return cluster;
    }

    /**
     * Checks the json object for all the required fields of a cluster object.
     *
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
     *
     * @param clusterJson JSON object that represents a cluster
     * @return The path of the cluster
     */
    private static String getPath(JsonObject clusterJson) {
        return clusterJson.get("path").getAsString();
    }

    /**
     * Creates an unconnected commodity under this cluster.
     *
     * @param name           Name of the commodity, it must form a unique identifier when concatenated with this cluster's path.
     * @param startLatitude  The pick up latitude of the commodity.
     * @param startLongitude The pick up longitude of the commodity.
     * @param endLatitude    The drop off latitude of the commodity.
     * @param endLongitude   The drop off longitude of the commodity.
     * @param status         The current status of the commodity. If <tt>null</tt> it will default to <tt>CommodityStatus.OFFLINE</tt>.
     * @param metadata       A JsonObject representing the metadata field of the commodity. If <tt>null</tt> it will default to an empty JsonObject.
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
     *
     * @param commodityJson a JSON object that represents a commodity
     * @param services      a pathfinder services object
     * @return A commodity with the values in the JSON object
     */
    private Commodity createCommodity(JsonObject commodityJson, PathfinderServices services) {
        return Commodity.getInstance(commodityJson, services);
    }

    /**
     * Returns a commodity directly under this cluster by its name.
     *
     * @param name The name of the commodity.
     * @return A commodity associated with that name if one exists, <tt>null</tt> if it doesn't exist.
     */
    public Commodity getCommodity(String name) {
        return this.commodities.get(this.getChildPath(name));
    }

    /**
     * Returns an immutable collection of this cluster's commodities.
     *
     * @return A collection of commodities.
     */
    public Collection<Commodity> getCommodities() {
        return Collections.<Commodity>unmodifiableCollection(this.commodities.values());
    }

    /**
     * Returns an immutable map of this cluster's commodities.
     *
     * @return A map of commodities.
     */
    public Map<String, Commodity> getCommoditiesMap() {
        return Collections.<String, Commodity>unmodifiableMap(this.commodities);
    }

    /**
     * Sets this cluster's commodities.
     *
     * @param commodities an iterable collection of commodities
     */
    private void setCommodities(Iterable<Commodity> commodities) {
        for (Commodity commodity : commodities) {
            this.commodities.put(commodity.getPath(), commodity);
        }
    }

    /**
     * Sets this cluster's commodities.
     *
     * @param commodities a map of commodities
     */
    private void setCommodities(Map<String, Commodity> commodities) {
        this.commodities = commodities;
    }

    /**
     * Creates an unconnected subcluster under this cluster.
     *
     * @param name Name of the subcluster, it must form a unique identifier when concatenated with this cluster's path.
     * @return An unconnected subcluster.
     * @throws IllegalStateException when this cluster is not connected.
     */
    public Cluster createSubcluster(String name) {
        if (!this.isConnected()) {
            throw new IllegalStateException("The cluster is not connected on the Pathfinder server");
        }

        return Cluster.getInstance(this.getChildPath(name), this.getServices());
    }

    /**
     * Returns a direct descendant subcluster by its name.
     *
     * @param name The name of the subcluster
     * @return A subcluster associated with the provided name if exists, <tt>null</tt> if it doesn't exist.
     */
    public Cluster getSubcluster(String name) {
        return this.subclusters.get(this.getChildPath(name));
    }

    /**
     * Returns an immutable collection of this cluster's direct subclusters.
     *
     * @return An immutable collection of clusters.
     */
    public Collection<Cluster> getSubclusters() {
        return Collections.<Cluster>unmodifiableCollection(this.subclusters.values());
    }

    /**
     * Returns an immutable map of this cluster's direct subclusters.
     *
     * @return An immutable map of clusters.
     */
    public Map<String, Cluster> getSubclustersMap() {
        return Collections.<String, Cluster>unmodifiableMap(this.subclusters);
    }

    /**
     * Sets this cluster's sub-clusters.
     *
     * @param subclusters an iterable collection of clusters
     */
    private void setSubclusters(Iterable<Cluster> subclusters) {
        for (Cluster cluster : subclusters) {
            this.subclusters.put(cluster.getPath(), cluster);
        }
    }

    /**
     * Sets this cluster's sub-clusters.
     *
     * @param subclusters a map of clusters
     */
    private void setSubclusters(Map<String, Cluster> subclusters) {
        this.subclusters = subclusters;
    }

    /**
     * Creates an unconnected transport under this cluster.
     *
     * @param name      Name of the transport, it must form a unique identifier when concatenated with this cluster's path.
     * @param latitude  The current latitude of the transport.
     * @param longitude The current longitude of the transport.
     * @param status    The current status of the transport. If <tt>null</tt> it defaults to <tt>TransportStatus.OFFLINE</tt>
     * @param metadata  The transports's metadata field. If <tt>null</tt> it defaults to an empty JSON object.
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
     *
     * @param transportJson a JSON object that represents a transport.
     * @return A transport with the values in the JSON object.
     */
    private Transport createTransport(JsonObject transportJson) {
        return Transport.getInstance(transportJson, this.getServices());
    }

    /**
     * Returns a transport directly under this cluster by its name.
     *
     * @param name The name of the transport.
     * @return A transport associated with the provided name if exists, <tt>null</tt> if it doesn't exist.
     */
    public Transport getTransport(String name) {
        return this.transports.get(this.getChildPath(name));
    }

    /**
     * Returns an immutable collection of this cluster's transports.
     *
     * @return A collection of transports.
     */
    public Collection<Transport> getTransports() {
        return Collections.<Transport>unmodifiableCollection(this.transports.values());
    }

    /**
     * Returns an immutable map of this cluster's transports.
     *
     * @return A map of transports.
     */
    public Map<String, Transport> getTransportsMap() {
        return Collections.<String, Transport>unmodifiableMap(this.transports);
    }

    /**
     * Sets this cluster's transports.
     *
     * @param transports an iterable collection of transports
     */
    private void setTransports(Iterable<Transport> transports) {
        for (Transport transport : transports) {
            this.transports.put(transport.getPath(), transport);
        }
    }

    /**
     * Sets this cluster's transports.
     *
     * @param transports a map of transports
     */
    private void setTransports(Map<String, Transport> transports) {
        this.transports = transports;
    }

    /**
     * Sets this cluster's routes.
     *
     * @param routes a list of routes for this cluster
     */
    private void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    /**
     * Returns an immutable collection of this cluster's routes.
     *
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
    protected boolean updateFields(JsonObject json) {
        Map<String, Commodity> prevCommodities;
        Map<String, Cluster> prevSubclusters;
        Map<String, Transport> prevTransports;
        List<Commodity> updatedCommodities = new ArrayList<Commodity>();
        List<Cluster> updatedClusters = new ArrayList<Cluster>();
        List<Transport> updatedTransports = new ArrayList<Transport>();

        boolean updated = false;

        prevCommodities = this.getCommoditiesMap();
        Map<String, Commodity> commodityMap = new HashMap<String, Commodity>();
        if (json.has("commodities")) {
            JsonArray commodities = json.getAsJsonArray("commodities");

            for (JsonElement commodityJson : commodities) {
                String path = ((JsonObject) commodityJson).get("path").getAsString();
                Commodity commodity = Commodity.getInstance(path, this.getServices());

                if (commodity.notifyUpdate(null, (JsonObject) commodityJson)) {
                    updatedCommodities.add(commodity);
                }

                commodityMap.put(commodity.getPath(), commodity);
            }
        }

        prevSubclusters = this.getSubclustersMap();
        Map<String, Cluster> clusterMap = new HashMap<String, Cluster>();
        if (json.has("subClusters")) {
            JsonArray clusters = json.getAsJsonArray("subClusters");

            for (JsonElement clusterJson : clusters) {
                String path = ((JsonObject) clusterJson).get("path").getAsString();
                Cluster cluster = Cluster.getInstance(path, this.getServices());

                if (cluster.notifyUpdate(null, (JsonObject) clusterJson)) {
                    updatedClusters.add(cluster);
                }

                clusterMap.put(cluster.getPath(), cluster);
            }
        }

        prevTransports = this.getTransportsMap();
        Map<String, Transport> transportMap = new HashMap<String, Transport>();
        if (json.has("transports")) {
            JsonArray transports = json.getAsJsonArray("transports");

            for (JsonElement transportJson : transports) {
                String path = ((JsonObject) transportJson).get("path").getAsString();
                Transport transport = Transport.getInstance(path, this.getServices());

                if (transport.notifyUpdate(null, (JsonObject) transportJson)) {
                    updatedTransports.add(transport);
                }

                transportMap.put(transport.getPath(), transport);
            }
        }

        List<ClusterListener> listeners = this.getListeners();

        for (String path : clusterMap.keySet()) {
            if (!prevSubclusters.containsKey(path)) {
                for (ClusterListener listener : listeners) {
                    listener.subclusterAdded(clusterMap.get(path));
                }
            }
            updated = true;
        }

        for (String path : commodityMap.keySet()) {
            if (!prevCommodities.containsKey(path)) {
                for (ClusterListener listener : listeners) {
                    listener.commodityAdded(commodityMap.get(path));
                }
            }
            updated = true;
        }

        for (String path : transportMap.keySet()) {
            if (!prevTransports.containsKey(path)) {
                for (ClusterListener listener : listeners) {
                    listener.transportAdded(transportMap.get(path));
                }
            }
            updated = true;
        }

        for (String path : prevCommodities.keySet()) {
            if (!commodityMap.containsKey(path)) {
                for (ClusterListener listener : listeners) {
                    listener.commodityRemoved(prevCommodities.get(path));
                }
            }
            updated = true;
        }

        for (String path : prevSubclusters.keySet()) {
            if (!clusterMap.containsKey(path)) {
                for (ClusterListener listener : listeners) {
                    listener.subclusterRemoved(prevSubclusters.get(path));
                }
            }
            updated = true;
        }

        for (String path : prevTransports.keySet()) {
            if (!transportMap.containsKey(path)) {
                for (ClusterListener listener : listeners) {
                    listener.transportRemoved(prevTransports.get(path));
                }
            }
            updated = true;
        }

        for (Cluster cluster : updatedClusters) {
            for (ClusterListener listener : listeners) {
                listener.subclusterUpdated(cluster);
            }
            updated = true;
        }

        for (Commodity commodity : updatedCommodities) {
            for (ClusterListener listener : listeners) {
                listener.commodityUpdated(commodity);
            }
            updated = true;
        }

        for (Transport transport : updatedTransports) {
            for (ClusterListener listener : listeners) {
                listener.transportUpdated(transport);
            }
            updated = true;
        }

        this.setCommodities(commodityMap);
        this.setSubclusters(clusterMap);
        this.setTransports(transportMap);

        if (!updatedCommodities.isEmpty()) {
            for (ClusterListener listener : listeners) {
                listener.commoditiesUpdated(this.getCommodities());
            }
        }

        if (!updatedClusters.isEmpty()) {
            for (ClusterListener listener : listeners) {
                listener.subclustersUpdated(this.getSubclusters());
            }
        }

        if (!updatedTransports.isEmpty()) {
            for (ClusterListener listener : listeners) {
                listener.transportsUpdated(this.getTransports());
            }
        }

        String parentPath = this.getParentPath();
        Cluster parentCluster = Cluster.getInstance(parentPath, this.getServices());
        if (updated && parentCluster != null) {

            Collection<Cluster> clusters = parentCluster.getSubclusters();

            List<ClusterListener> clusterListeners = parentCluster.getListeners();
            for (ClusterListener listener : clusterListeners) {
                listener.subclusterUpdated(this);
                listener.subclustersUpdated(clusters);
            }
        }

        return updated;
    }

    /**
     * {@inheritDoc}
     */
    protected void route(JsonObject json, PathfinderServices services) {
        JsonArray routesJson = json.getAsJsonArray("route");
        List<Route> routes = new ArrayList<Route>();

        for (JsonElement route : routesJson) {
            routes.add(new Route((JsonObject) route, services));
        }

        this.setRoutes(routes);

        for (ClusterListener listener : this.getListeners()) {
            listener.routed(this.getRoutes());
        }
    }
}
