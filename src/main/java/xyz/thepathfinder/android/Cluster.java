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

public class Cluster extends SubscribableCrudModel<ClusterListener> {

    private static final String MODEL = Pathfinder.CLUSTER;

    private Map<String, Transport> transports;
    private Map<String, Commodity> commodities;
    private Map<String, Cluster> subclusters;

    private List<Route> routes;

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

    protected static Cluster getInstance(String path, PathfinderServices services) {
        Cluster cluster = (Cluster) services.getRegistry().getModel(path, Cluster.MODEL);

        if(cluster == null) {
            cluster = new Cluster(path, services);
        }

        return cluster;
    }

    protected static Cluster getInstance(JsonObject clusterJson, PathfinderServices services) {
        Cluster.checkClusterFields(clusterJson);

        String path = Cluster.getPath(clusterJson);
        Cluster cluster = Cluster.getInstance(path, services);

        cluster.setClusterFields(clusterJson);
        return cluster;
    }

    private static void checkClusterFields(JsonObject clusterJson) {
        if(!clusterJson.has("path")) {
            throw new ClassCastException("No cluster path was found in the JSON");
        }

        if(!clusterJson.has("transports")) {
            throw new ClassCastException("No list of transports was found in the JSON");
        }

        if(!clusterJson.get("transports").isJsonArray()) {
            throw new ClassCastException("Transports were not a list in the JSON");
        }

        if(!clusterJson.has("commodities")) {
            throw new ClassCastException("No list of commodities was found in the JSON");
        }

        if(!clusterJson.get("commodities").isJsonArray()) {
            throw new ClassCastException("Commodities were not a list in the JSON");
        }

        if(!clusterJson.has("subClusters")) {
            throw new ClassCastException("No list of subclusters was found in the JSON");
        }

        if(!clusterJson.get("subClusters").isJsonArray()) {
            throw new ClassCastException("Subclusters were not a list in the JSON");
        }
    }

    private static String getPath(JsonObject clusterJson) {
        return clusterJson.get("path").getAsString();
    }

    private static List<Commodity> getCommodities(JsonObject clusterJson, PathfinderServices services) {
        List<Commodity> commodities = new LinkedList<Commodity>();
        for(JsonElement commodity: clusterJson.getAsJsonArray("commodities")) {
            commodities.add(Commodity.getInstance(commodity.getAsJsonObject(), services));
        }
        return commodities;
    }

    private static List<Cluster> getSubclusters(JsonObject clusterJson, PathfinderServices services) {
        List<Cluster> clusters = new LinkedList<Cluster>();
        for(JsonElement cluster: clusterJson.getAsJsonArray("subClusters")) {
            clusters.add(Cluster.getInstance(cluster.getAsJsonObject(), services));
        }
        return clusters;
    }

    private static List<Transport> getTransports(JsonObject clusterJson, PathfinderServices services) {
        List<Transport> transports = new LinkedList<Transport>();
        for(JsonElement transport: clusterJson.getAsJsonArray("transports")) {
            transports.add(Transport.getInstance(transport.getAsJsonObject(), services));
        }
        return transports;
    }

    private void setClusterFields(JsonObject clusterJson) {
        List<Commodity> commodities = Cluster.getCommodities(clusterJson, this.getServices());
        this.setCommodities(commodities);

        List<Transport> transports = Cluster.getTransports(clusterJson, this.getServices());
        this.setTransports(transports);

        List<Cluster> subclusters = Cluster.getSubclusters(clusterJson, this.getServices());
        this.setSubclusters(subclusters);
    }

    public Commodity createCommodity(String name, double startLatitude, double startLongitude, double endLatitude, double endLongitude, CommodityStatus status, JsonObject metadata) {
        if (!this.isConnected()) {
            throw new IllegalStateException("Not connected to cluster, cannot create commodity");
        }

        String path = this.getChildPath(name);
        return new Commodity(path, startLatitude, startLongitude, endLatitude, endLongitude, status, metadata, this.getServices());
    }

    private Commodity createCommodity(JsonObject json, PathfinderServices services) {
        return Commodity.getInstance(json, services);
    }

    public Collection<Commodity> getCommodities() {
        return Collections.unmodifiableCollection(this.commodities.values());
    }

    public Commodity removeCommodity(Commodity commodity) {
        return this.commodities.remove(commodity.getPath());
    }

    private void setCommodities(Iterable<Commodity> commodities) {
        for(Commodity commodity: commodities) {
            this.commodities.put(commodity.getPath(), commodity);
        }
    }

    public Cluster createSubcluster(String name) {
        return Cluster.getInstance(this.getChildPath(name), this.getServices());
    }

    public Cluster getSubcluster(String name) {
        return this.subclusters.get(this.getChildPath(name));
    }

    public Collection<Cluster> getSubclusters() {
        return Collections.unmodifiableCollection(this.subclusters.values());
    }

    //TODO think about how to deal with the remove methods
    public Cluster removeSubcluster(Cluster cluster) {
        return this.subclusters.remove(cluster.getPath());
    }

    private void setSubclusters(Iterable<Cluster> subclusters) {
        for(Cluster cluster: subclusters) {
            this.subclusters.put(cluster.getPath(), cluster);
        }
    }

    public Transport createTransport(String name, double latitude, double longitude, TransportStatus status, JsonObject metadata) {
        if (!this.isConnected()) {
            throw new IllegalStateException("Not connected to cluster, cannot create transport");
        }

        String path = this.getChildPath(name);
        return new Transport(path, latitude, longitude, status, metadata, this.getServices());
    }

    private Transport createTransport(JsonObject json) {
        return Transport.getInstance(json, this.getServices());
    }

    public Transport getTransport(String name) {
        return this.transports.get(this.getChildPath(name));
    }

    public Collection<Transport> getTransports() {
        return Collections.unmodifiableCollection(this.transports.values());
    }

    public Transport removeTransport(Transport transport) {
        return this.transports.remove(transport.getPath());
    }

    private void setTransports(Iterable<Transport> transports) {
        for(Transport transport: transports) {
            this.transports.put(transport.getPath(), transport);
        }
    }

    private void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public List<Route> getRoutes() {
        return Collections.unmodifiableList(this.routes);
    }

    public void subscribe() {
        JsonObject modelJson = new JsonObject();
        modelJson.addProperty("path", this.getPath());

        super.subscribe(modelJson);
    }

    @Override
    protected String getModel() {
        return Cluster.MODEL;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("parentPath", this.getParentPath());
        return json;
    }

    @Override
    protected void notifyUpdate(JsonObject json) {
        // IMPLEMENTME: 12/12/15
    }
}
