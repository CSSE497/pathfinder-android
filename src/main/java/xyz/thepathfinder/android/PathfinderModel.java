package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public abstract class PathfinderModel {

    private Path path;
    private boolean isConnected;

    public PathfinderModel(String path) {
        this.path = new Path(path);
        this.isConnected = false;
    }

    public String getPath() {
        return this.path.getPath();
    }

    protected String getChildPath(String name) {
        return this.path.getChildPath(name);
    }

    public String getParentPath() {
        return this.path.getParentPath();
    }

    public Cluster getParent() {
        String parentPath = this.getParentPath();
        return Cluster.getInstance(parentPath);
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    protected abstract String getModel();
    protected abstract JsonObject toJson();

}
