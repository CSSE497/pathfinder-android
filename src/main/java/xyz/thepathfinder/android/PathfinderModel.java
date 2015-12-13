package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public abstract class PathfinderModel {

    private Path path;

    public PathfinderModel(String path) {
        this.path = new Path(path);
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

    protected abstract JsonObject toJson();
    protected abstract String getModel();
    public abstract boolean isConnected();
}
