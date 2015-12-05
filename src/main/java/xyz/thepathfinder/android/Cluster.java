package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cluster extends PathfinderListenable<ClusterListener> {

    public static final String DEFAULT_PATH = "/";

    private String path;
    private PathfinderConnection connection;

    private Long id;
    private Long parentId;

    private Map<Long, Transport> transports;
    private Map<Long, Commodity> commodities;
    private Map<Long, Cluster> subclusters;
    private List<Route> routes;

    private boolean isSubscribed;

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
        this.path = path;
        this.connection = connection;

        this.id = null;
        this.parentId = parentId;

        this.transports = new HashMap<Long, Transport>();
        this.commodities = new HashMap<Long, Commodity>();
        this.subclusters = new HashMap<Long, Cluster>();
        this.routes = null;

        this.isSubscribed = false;
    }

    public boolean isConnected() {
        return this.getId() != null;
    }

    public boolean isSubscribed() {
        return this.isSubscribed;
    }

    public void connect() {
        //TODO implement
    }

    public void create() {
        //TODO implement
    }

    public void delete() {
        //TODO implement
    }

    public void update() {
        //TODO implement
    }

    public void subscribe() {
        //TODO implement
    }

    public void unsubscribe() {
        //TODO implement
    }

    public Cluster getSubcluster(Long id) {
        return this.subclusters.get(id);
    }

    public Cluster createSubcluster() {
        if(this.isConnected()) {
            return new Cluster(this.getId(), this.connection);
        } else {
            return null;
        }
    }

    public Collection<Cluster> getSubclusters() {
        return Collections.unmodifiableCollection(this.subclusters.values());
    }

    public Cluster removeSubcluster(Long id) {
        return this.subclusters.remove(id);
    }

    public Cluster parentCluster() {
        if(this.path.equals(Cluster.DEFAULT_PATH)) {
            return null;
        } else {
            return new Cluster(this.parentId, this.connection);
        }
    }

    protected void setId(long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    protected void setParentId(Long id) {
        this.parentId = id;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public Transport createTransport(Transport transport) {
        if(this.isConnected()) {
            return new Transport(this.getId(), this.connection);
        } else {
            return null;
        }
    }

    public Collection<Transport> getTransports() {
        return Collections.unmodifiableCollection(this.transports.values());
    }

    public Transport removeTransport(Long id) {
        return this.transports.remove(id);
    }

    public Commodity createCommodity(Commodity commodity) {
        if(this.isConnected()) {
            return new Commodity(this.getId(), this.connection);
        } else {
            return null;
        }
    }

    public Collection<Commodity> getCommodities() {
        return Collections.unmodifiableCollection(this.commodities.values());
    }

    public Commodity removeCommodity(Long id) {
        return this.commodities.remove(id);
    }

    protected void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public List<Route> getRoutes() {
        return Collections.unmodifiableList(this.routes);
    }

    protected void notifyUpdate(JsonObject json) {
        //TODO implement
    }
}
