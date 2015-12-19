package xyz.thepathfinder.android;

import com.google.gson.JsonObject;

public abstract class Model {

    private final Path path;
    private final PathfinderServices services;
    private boolean isConnected;

    public Model(String path, PathfinderServices services) {
        this.path = new Path(path);
        this.services = services;
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
        return Cluster.getInstance(parentPath, services);
    }

    protected PathfinderServices getServices() {
        return this.services;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    /**
     * Returns the name of the model
     * @return name of model
     */
    protected abstract String getModel();

    /**
     * Returns the value used in create request to the Pathfinder server
     * @return the value JSON
     */
    protected abstract JsonObject createValueJson();

}
