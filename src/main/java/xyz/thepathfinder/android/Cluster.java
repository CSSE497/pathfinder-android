package xyz.thepathfinder.android;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Cluster extends SubscribableCrudModel<ClusterListener> {

    public static final String DEFAULT_PATH = "/";

    protected static final String MODEL = "Cluster";

    private String path;

    private Long id;
    private Long parentId;

    private List<Transport> transports;
    private List<Commodity> commodities;
    private List<Cluster> subclusters;
    private List<Route> routes;

    protected Cluster(PathfinderConnection connection) {
        this(null, Cluster.DEFAULT_PATH, connection);
    }

    protected Cluster(Long parentId, PathfinderConnection connection) {
        this(parentId, null, connection);
    }

    protected Cluster(String path, PathfinderConnection connection) {
        this(null, path, connection);
    }

    protected Cluster(Long parentId, String path, PathfinderConnection connection) {
        super(connection);
        this.path = path;

        this.id = null;
        this.parentId = parentId;

        this.transports = null;
        this.commodities = null;
        this.subclusters = null;
        this.routes = null;
    }

    protected Cluster(JsonObject clusterJson, PathfinderConnection connection) {
        super(connection);

        this.checkClusterFields(clusterJson);
        this.setClusterFields(clusterJson);
    }

    private void checkClusterFields(JsonObject clusterJson) {
        if(!clusterJson.has("id")) {
            throw new ClassCastException("No cluster ID was found in the JSON");
        }

        if(!clusterJson.has("parent")) {
            throw new ClassCastException("No parent cluster ID was found in the JSON");
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

    private void setClusterFields(JsonObject clusterJson) {
        this.setId(clusterJson.get("id").getAsLong());

        if(clusterJson.get("parent").isJsonNull()) {
            this.setParentId(null);
        } else {
            this.setParentId(clusterJson.get("parent").getAsLong());
        }

        LinkedList<Commodity> commodities = new LinkedList<Commodity>();
        for(JsonElement commodity: clusterJson.getAsJsonArray("commodities")) {
            commodities.add(new Commodity(commodity.getAsJsonObject(), this.getConnection()));
        }
        this.setCommodities(commodities);

        LinkedList<Transport> transports = new LinkedList<Transport>();
        for(JsonElement transport: clusterJson.getAsJsonArray("transports")) {
            transports.add(new Transport(transport.getAsJsonObject(), this.getConnection()));
        }
        this.setTransports(transports);

        LinkedList<Cluster> subclusters = new LinkedList<Cluster>();
        for(JsonElement subcluster: clusterJson.getAsJsonArray("subClusters")) {
            subclusters.add(new Cluster(subcluster.getAsJsonObject(), this.getConnection()));
        }
        this.setSubclusters(subclusters);
    }

    @Override
    public boolean isConnected() {
        return this.getId() != null;
    }

    public Cluster createSubcluster() {
        if (this.isConnected()) {
            return new Cluster(this.getId(), this.getConnection());
        } else {
            return null;
        }
    }

    public List<Cluster> getSubclusters() {
        return Collections.unmodifiableList(this.subclusters);
    }

    public boolean removeSubcluster(Cluster cluster) {
        return this.subclusters.remove(cluster);
    }

    private void setSubclusters(List<Cluster> subclusters) {
        this.subclusters = subclusters;
    }

    public Cluster parentCluster() {
        if (this.path.equals(Cluster.DEFAULT_PATH)) {
            return null;
        } else {
            return new Cluster(this.parentId, this.getConnection());
        }
    }

    private void setId(long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    private void setParentId(Long id) {
        this.parentId = id;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public Transport createTransport() {
        if (this.isConnected()) {
            return new Transport(this.getId(), this.getConnection());
        } else {
            return null;
        }
    }

    private Transport createTransport(JsonObject json) {
        return new Transport(json, this.getConnection());
    }

    public List<Transport> getTransports() {
        return Collections.unmodifiableList(this.transports);
    }

    public boolean removeTransport(Transport transport) {
        return this.transports.remove(transport);
    }

    private void setTransports(List<Transport> transports) {
        this.transports = transports;
    }

    public Commodity createCommodity() {
        if (this.isConnected()) {
            return new Commodity(this.getId(), this.getConnection());
        } else {
            return null;
        }
    }

    private Commodity createCommodity(JsonObject json) {
        return new Commodity(json, this.getConnection());
    }

    public List<Commodity> getCommodities() {
        return Collections.unmodifiableList(this.commodities);
    }

    public boolean removeCommodity(Commodity commodity) {
        return this.commodities.remove(commodity);
    }

    private void setCommodities(List<Commodity> commodities) {
        this.commodities = commodities;
    }

    private void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public List<Route> getRoutes() {
        return Collections.unmodifiableList(this.routes);
    }

    @Override
    protected String getModel() {
        return Cluster.MODEL;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();
        return json;
    }

    public void subscribe() {
        this.subscribe(SubscribableClusterModel.COMMODITY);
        this.subscribe(SubscribableClusterModel.TRANSPORT);
    }

    public void subscribe(SubscribableClusterModel model) {
        JsonObject modelJson = new JsonObject();
        modelJson.addProperty("model", model.toString());
        modelJson.addProperty("clusterId", this.getId());

        super.subscribe(modelJson);
    }

    @Override
    protected void notifyUpdate(JsonObject json) {
        //TODO implement
    }

    @Override
    public void connect() {
        if(this.path != null && this.path.equals(Cluster.DEFAULT_PATH)) {
            connectDefaultCluster();
        } else {
            super.connect();
        }
    }

    private void connectDefaultCluster() {
        JsonObject jsonId = new JsonObject();
        jsonId.addProperty("id", this.getConnection().getApplictionIdentifier());

        JsonObject json = new JsonObject();
        json.add("getApplicationCluster", jsonId);
        this.getConnection().sendMessage(json.toString());
    }
}
