package xyz.thepathfinder.android;

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

    protected Cluster(PathfinderConnection connection) {
        this(null, this.DEFAULT_PATH, connection);
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
    }

    public Cluster getSubcluster(Long id) {
        return this.subclusters.get(id);
    }

    public Cluster createSubcluster() {
        return new Cluster(this.id, this.connection);
    }

    public Collection<Cluster> getSubclusters() {
        return Collections.unmodifiableCollection(this.subclusters.values());
    }

    public Cluster removeSubcluster(Long id) {
        return this.subclusters.remove(id);
    }

    public Cluster parentCluster() {
        if(this.path.equals(this.DEFAULT_PATH)) {
            return null;
        } else {
            String parentPath = this.path.substring(0, this.path.lastIndexOf("/"));
            return new Cluster(parentPath, this.connection);
        }
    }

}
