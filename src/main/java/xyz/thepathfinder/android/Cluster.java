package xyz.thepathfinder.android;

public class Cluster {

    public static final String DEFAULT_PATH = "/";

    private String path;
    private PathfinderConnection connection;

    public Cluster(String path, PathfinderConnection connection) {
        this.path = path;
        this.connection = connection;
    }

    public Cluster createSubCluster(String path) {
        String subClusterPath;
        if(this.path.equals(this.DEFAULT_PATH)) {
            subClusterPath = this.path + path;
        } else {
            subClusterPath = this.path + "/" + path;
        }
        return new Cluster(subClusterPath, this.connection);
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
